package org.bibletranslationtools.maui.common.fileprocessor

import org.bibletranslationtools.maui.common.data.FileResult
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.extensions.MediaExtensions
import org.bibletranslationtools.maui.common.validators.Mp3Validator
import java.io.File
import java.lang.IllegalArgumentException
import java.util.Queue

class Mp3Processor : FileProcessor() {
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

        if (ext != MediaExtensions.MP3) {
            return null
        }

        val media = try {
            Mp3Validator(file).validate()
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