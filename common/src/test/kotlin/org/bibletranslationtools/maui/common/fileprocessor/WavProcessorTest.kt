package org.bibletranslationtools.maui.common.fileprocessor

import io.mockk.mockk
import org.bibletranslationtools.maui.common.audio.ISoxBinaryProvider
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.util.Queue
import java.util.LinkedList
import kotlin.io.path.createTempDirectory as createTempDir

class WavProcessorTest {
    private val directoryProvider = mockk<IDirectoryProvider>()
    private val soxBinaryProvider = mockk<ISoxBinaryProvider>()

    private lateinit var queue: Queue<ProcessFile>
    private lateinit var tempDir: File
    private val wavFileName = "en_ulb_b41_mat_c01.wav"

    @Before
    fun setUp() {
        tempDir = createTempDir().toFile()
        queue = LinkedList()
    }

    @After
    fun cleanUp() {
        tempDir.deleteRecursively()
        queue.clear()
    }

    @Test
    fun testProcessGoodFile() {
        val file = getTestFile(wavFileName)
        val result = WavProcessor(directoryProvider, soxBinaryProvider).process(file, queue)

        assertEquals(FileStatus.PROCESSED, result?.status)
        assertEquals(0, queue.size)
    }

    @Test
    fun testCustomResource() {
        val file = copyAndRename(wavFileName, "en_reg_b41_mat_c01.wav")
        val result = WavProcessor(directoryProvider, soxBinaryProvider).process(file, queue)

        assertEquals(FileStatus.PROCESSED, result?.status)
        assertEquals(0, queue.size)
    }

    @Test
    fun testProcessBadFile() {
        val file = getTestFile("fake.wav")
        val result = WavProcessor(directoryProvider, soxBinaryProvider).process(file, queue)

        assertEquals(FileStatus.REJECTED, result?.status)
        assertEquals(0, queue.size)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
                ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }

    private fun copyAndRename(source: String, target: String): File {
        val wav = getTestFile(source)
        val newFile = tempDir.resolve(target)
        wav.copyTo(newFile, true)

        return newFile
    }
}