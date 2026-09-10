package org.bibletranslationtools.maui.common.fileprocessor

import org.bibletranslationtools.maui.common.data.FileResult
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.extensions.MediaExtensions
import org.bibletranslationtools.maui.common.validators.JpgValidator
import java.io.File
import java.lang.IllegalArgumentException
import java.util.Queue

class JpgProcessor : FileProcessor() {
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

        if (ext != MediaExtensions.JPG) {
            return null
        }

        val media = try {
            JpgValidator(file).validate()
            getMedia(file).copy(parentFile = parentFile)
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
}