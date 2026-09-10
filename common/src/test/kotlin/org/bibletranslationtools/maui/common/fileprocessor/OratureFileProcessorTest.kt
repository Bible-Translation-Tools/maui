package org.bibletranslationtools.maui.common.fileprocessor

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.MediaExtension
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.bibletranslationtools.maui.common.usecases.ParseFileName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.io.path.createTempDirectory as createTempDir

class OratureFileProcessorTest {
    private val oratureFileName = "orature_file.zip"
    private val expectedWavFiles = 2

    private val directoryProvider = mockk<IDirectoryProvider> {
        every { createCacheDirectory(any()) } returns createTempDir("cache").toFile().apply {
            deleteOnExit()
        }
    }

    @Test
    fun testExtractAudioFiles() {
        val oratureFile = getOratureFile(oratureFileName)
        val extension = MediaExtension.WAV.toString()
        val files = OratureFileProcessor(directoryProvider).extractAudio(oratureFile, extension)

        assertEquals(expectedWavFiles, files.size)

        files.forEach { file ->
            val result = ParseFileName(file).parse()

            // imported file names should be valid for parser
            assertNotNull(result.language)
            assertNotNull(result.resourceType)
            assertNotNull(result.book)
            assertNotNull(result.chapter)
        }
    }

    @Test
    fun testProcessGoodOratureFile() {
        val oratureFile = getOratureFile(oratureFileName)
        val queue: Queue<ProcessFile> = LinkedList()
        val result = OratureFileProcessor(directoryProvider).process(oratureFile, queue)

        assertEquals(null, result)
        assertEquals(2, queue.size)
    }

    @Test
    fun testProcessBadOratureFile() {
        val anyZipFile = createTempFile(suffix = ".zip")
        val queue: Queue<ProcessFile> = LinkedList()
        val result = OratureFileProcessor(directoryProvider).process(anyZipFile, queue)

        assertEquals(FileStatus.REJECTED, result?.status)
        assertEquals(0, queue.size)
    }

    private fun getOratureFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
                ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}