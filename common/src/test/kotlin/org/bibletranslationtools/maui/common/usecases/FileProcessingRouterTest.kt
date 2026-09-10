package org.bibletranslationtools.maui.common.usecases

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.audio.ISoxBinaryProvider
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.createTempDirectory as createTempDir

class FileProcessingRouterTest {
    private val oratureFile = "orature_file.zip" // orature file contains 2 bad wav files
    private val wavFile = "en_ulb_b41_mat_c01.wav"
    private val badFile = "fake.jpg"
    private val expectedResultSize = 4

    private val directoryProvider = mockk<IDirectoryProvider> {
        every { createCacheDirectory(any()) } returns createTempDir("cache").toFile().apply {
            deleteOnExit()
        }
    }
    private val soxBinaryProvider = mockk<ISoxBinaryProvider>()

    @Test
    fun testHandleFiles() {
        val files = listOf(
                getTestFile(oratureFile),
                getTestFile(wavFile),
                getTestFile(badFile)
        )
        val result = FileProcessingRouter(directoryProvider, soxBinaryProvider).handleFiles(files)
        val errorFileCount = result.filter {
            it.status == FileStatus.REJECTED
        }.size
        val successFileCount = result.filter {
            it.status == FileStatus.PROCESSED
        }.size

        assertEquals(expectedResultSize, result.size)
        assertEquals(1, successFileCount)
        assertEquals(3, errorFileCount)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
                ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}