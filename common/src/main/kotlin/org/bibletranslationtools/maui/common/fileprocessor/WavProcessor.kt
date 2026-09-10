/**
 * Copyright (C) 2020-2024 Wycliffe Associates
 *
 * This file is part of Orature.
 *
 * Orature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Orature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Orature.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.bibletranslationtools.maui.common.fileprocessor

import ie.corballis.sox.Sox
import org.bibletranslationtools.maui.common.audio.ISoxBinaryProvider
import org.bibletranslationtools.maui.common.data.FileResult
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.extensions.MediaExtensions
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.bibletranslationtools.maui.common.validators.WavValidator
import org.wycliffeassociates.otter.common.audio.DEFAULT_BITS_PER_SAMPLE
import org.wycliffeassociates.otter.common.audio.DEFAULT_CHANNELS
import org.wycliffeassociates.otter.common.audio.DEFAULT_SAMPLE_RATE
import org.wycliffeassociates.otter.common.audio.wav.WavFile
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

class WavProcessor(
    private val directoryProvider: IDirectoryProvider,
    private val soxBinaryProvider: ISoxBinaryProvider
) : FileProcessor() {
    override fun process(
        file: File,
        fileQueue: Queue<ProcessFile>,
        parentFile: File?
    ): FileResult? {
        val ext = try {
            MediaExtensions.of(file.extension)
        } catch (ex: IllegalArgumentException) {
            null
        }

        if (ext != MediaExtensions.WAV) {
            return null
        }

        val media = try {
            val normalized = normalizedFile(file)
            val normalizedParent = parentFile ?: normalized?.let { file }

            WavValidator(normalized ?: file).validate()
            getMedia(normalized ?: file).copy(parentFile = normalizedParent)
        } catch (ex: Exception) {
            Media(
                file = file,
                status = FileStatus.REJECTED,
                statusMessage = ex.message,
                parentFile = parentFile
            )
        }

        return FileResult(file, media.status, media.statusMessage, media)
    }

    /**
     * Convert wav file so that sample rate, bit-depth and number of channels
     * be the default values. The original file will be untouched. Converted file will be saved in cache folder.
     */
    private fun normalizedFile(file: File): File? {
        val wavFile = WavFile(file)

        return if (wavFile.sampleRate != DEFAULT_SAMPLE_RATE ||
            wavFile.channels != DEFAULT_CHANNELS ||
            wavFile.bitsPerSample != DEFAULT_BITS_PER_SAMPLE
        ) {
            val uuid = UUID.randomUUID()
            val cacheDir = directoryProvider.createCacheDirectory(uuid.toString())
            val newFile = cacheDir.resolve(file.name)
            val sox = Sox(soxBinaryProvider.getPath())
            sox
                .inputFile(file.absolutePath)
                .sampleRate(DEFAULT_SAMPLE_RATE)
                .bits(DEFAULT_BITS_PER_SAMPLE)
                .argument("--channels", DEFAULT_CHANNELS.toString())
                .outputFile(newFile.absolutePath)
                .execute()

            // Reinsert verse markers, because sox removes them during conversion
            val cues = wavFile.getCues()
            val newWavFile = WavFile(newFile)
            cues.forEach {
                val normalizedLocation = (it.location.toLong() * DEFAULT_SAMPLE_RATE) / wavFile.sampleRate
                newWavFile.addCue(normalizedLocation.toInt(), it.label)
            }
            newWavFile.update()

            newFile
        } else null
    }
}