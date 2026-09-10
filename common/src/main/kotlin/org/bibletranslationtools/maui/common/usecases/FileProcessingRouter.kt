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
package org.bibletranslationtools.maui.common.usecases

import org.bibletranslationtools.maui.common.audio.ISoxBinaryProvider
import org.bibletranslationtools.maui.common.data.FileResult
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.fileprocessor.*
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import java.io.File
import java.io.IOException
import java.util.Queue
import java.util.LinkedList
import javax.inject.Inject

class FileProcessingRouter @Inject constructor(
    private val directoryProvider: IDirectoryProvider,
    private val soxBinaryProvider: ISoxBinaryProvider
) {

    private val processors: List<FileProcessor> = getProcessors()
    private val fileQueue: Queue<ProcessFile> = LinkedList()

    @Throws(IOException::class)
    fun handleFiles(files: List<File>): List<FileResult> {
        val resultList = mutableListOf<FileResult>()
        fileQueue.addAll(files.map { ProcessFile(it, null) })

        while (fileQueue.isNotEmpty()) {
            processFile(fileQueue.poll(), resultList)
        }

        return resultList
    }

    private fun processFile(file: ProcessFile, resultList: MutableList<FileResult>) {
        processors.forEach {
            it.process(file.self, fileQueue, file.parent)?.let { result ->
                resultList.add(result)
            }
        }
    }

    private fun getProcessors(): List<FileProcessor> {
        return listOf(
            CueProcessor(),
            JpgProcessor(),
            Mp3Processor(),
            TrProcessor(directoryProvider),
            WavProcessor(directoryProvider, soxBinaryProvider),
            OratureFileProcessor(directoryProvider)
        )
    }
}