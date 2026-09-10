package org.bibletranslationtools.maui.common.fileprocessor

import org.bibletranslationtools.maui.common.data.FileResult
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.usecases.ParseFileName
import java.io.File
import java.util.Queue

abstract class FileProcessor {
    abstract fun process(
        file: File,
        fileQueue: Queue<ProcessFile>,
        parentFile: File? = null
    ): FileResult?

    protected fun getMedia(file: File): Media {
        return try {
            ParseFileName(file).parse()
        } catch (e: Exception) {
            throw Exception("Error while parsing file name.")
        }
    }
}