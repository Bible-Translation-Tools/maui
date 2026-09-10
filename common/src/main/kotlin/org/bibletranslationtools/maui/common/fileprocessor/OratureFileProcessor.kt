package org.bibletranslationtools.maui.common.fileprocessor

import org.bibletranslationtools.maui.common.data.FileResult
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.MediaExtension
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.extensions.MediaExtensions
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.bibletranslationtools.maui.common.validators.OratureValidator
import org.slf4j.LoggerFactory
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.Queue
import java.util.UUID

class OratureFileProcessor(private val directoryProvider: IDirectoryProvider) : FileProcessor() {
    private val logger = LoggerFactory.getLogger(javaClass)

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

        if (ext != MediaExtensions.ORATURE) {
            return null
        }

        return try {
            OratureValidator(file).validate()
            val extension = MediaExtension.WAV.toString()
            val extractedFiles = extractAudio(file, extension)
            fileQueue.addAll(extractedFiles.map { ProcessFile(it, file) })

            // Return null on success here because
            // we don't want to add this orature file to the result list;
            // only media files inside the orature file will be added later
            null
        } catch (ex: Exception) {
            logger.error("An error occurred in process", ex)
            FileResult(file, FileStatus.REJECTED, ex.message)
        }
    }

    @Throws(IOException::class)
    fun extractAudio(file: File, extension: String): List<File> {
        var cacheDir: File?

        ResourceContainer.load(file).use { rc ->
            val content = rc.getProjectContent(extension = extension)
                    ?: return listOf()

            val uuid = UUID.randomUUID()
            cacheDir = directoryProvider.createCacheDirectory(uuid.toString())

            content.streams.forEach { entry ->
                // resolve chapter file name for parser compatibility
                val normalizedFileName = File(entry.key).name.replace("_meta", "")
                val destFile = cacheDir!!.resolve(normalizedFileName)

                entry.value.buffered().use { input ->
                    destFile.outputStream().buffered().use { output ->
                        output.write(input.readBytes())
                    }
                }
            }
        }

        return cacheDir?.listFiles()?.toList() ?: listOf()
    }
}
