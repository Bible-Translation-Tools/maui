package org.bibletranslationtools.maui.common.fileprocessor

import org.bibletranslationtools.maui.common.data.FileResult
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.MediaExtension
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
        fileQueue: Queue<File>,
        resultList: MutableList<FileResult>
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
            fileQueue.addAll(extractedFiles)

            // Return null on success here because
            // we don't want to add this orature file to the result list;
            // only media files inside the orature file will be added
            null
        } catch (ex: Exception) {
            logger.error("An error occurred in process", ex)
            FileResult(FileStatus.ERROR, ex.message, null, file)
        }
    }

    @Throws(IOException::class)
    fun extractAudio(file: File, extension: String): List<File> {
        val uuid = UUID.randomUUID()
        val tempDir = directoryProvider.createCacheDirectory(uuid.toString())

        ResourceContainer.load(file).use { rc ->
            val content = rc.getProjectContent(extension = extension)
                    ?: return listOf()

            content.streams.forEach { entry ->
                // resolve chapter file name for parser compatibility
                val normalizedFileName = File(entry.key).name.replace("_meta", "")
                val destFile = tempDir.resolve(normalizedFileName)
                destFile.deleteOnExit()

                entry.value.buffered().use { input ->
                    destFile.outputStream().buffered().use { output ->
                        output.write(input.readBytes())
                    }
                }
            }
        }

        return tempDir.listFiles()?.toList() ?: listOf()
    }
}
