package org.bibletranslationtools.maui.common.validators

import org.bibletranslationtools.maui.common.audio.BttrChunk
import org.bibletranslationtools.maui.common.audio.BttrMetadata
import org.wycliffeassociates.otter.common.audio.wav.CueChunk
import org.wycliffeassociates.otter.common.audio.wav.InvalidWavFileException
import org.wycliffeassociates.otter.common.audio.wav.WavFile
import org.wycliffeassociates.otter.common.audio.wav.WavMetadata
import org.wycliffeassociates.otter.common.audio.wav.WavType
import java.io.File
import java.util.regex.Pattern

class WavValidator(private val file: File) : IValidator {
    private lateinit var wav: WavFile

    /**
     * Validates WAV file
     * @throws InvalidWavFileException
     */
    override fun validate() {
        when {
            isChunkOrVerse() -> {
                val bttrChunk = BttrChunk()
                val wavMetadata = WavMetadata(listOf(bttrChunk))
                wav = WavFile(file, wavMetadata)

                if (!validateBttrMetadata(bttrChunk.metadata)) {
                    throw InvalidWavFileException("Chunk has corrupt metadata")
                }
            }
            isChapter() -> {
                val cueChunk = CueChunk()
                val wavMetadata = WavMetadata(listOf(cueChunk))
                wav = WavFile(file, wavMetadata)
            }
            else -> wav = WavFile(file)
        }

        if (wav.wavType == WavType.WAV_WITH_EXTENSION) {
            throw InvalidWavFileException("wav file with custom extension is not supported")
        }
    }

    private fun validateBttrMetadata(metadata: BttrMetadata): Boolean {
        return metadata.language.isBlank()
            .or(metadata.anthology.isBlank())
            .or(metadata.version.isBlank())
            .or(metadata.bookNumber.isBlank())
            .or(metadata.slug.isBlank())
            .or(metadata.mode.isBlank())
            .or(metadata.chapter.isBlank())
            .or(metadata.startv.isBlank())
            .or(metadata.endv.isBlank())
            .or(metadata.markers.isEmpty())
            .not()
    }

    private fun isChunkOrVerse(): Boolean {
        val chunkPattern = "_v\\d{1,3}(?:-\\d{1,3})?"
        val pattern = Pattern.compile(chunkPattern, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(file.nameWithoutExtension)

        return matcher.find()
    }

    private fun isChapter(): Boolean {
        val chapterPattern = "_c(\\d{1,3})"
        val pattern = Pattern.compile(chapterPattern, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(file.nameWithoutExtension)

        return matcher.find()
    }
}
