package org.bibletranslationtools.maui.common.fileprocessor

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.io.path.createTempDirectory as createTempDir

class TrProcessorTest {
    private lateinit var queue: Queue<ProcessFile>

    private val directoryProvider = mockk<IDirectoryProvider> {
        every { createTempDirectory(any()) } returns createTempDir("cache").toFile().apply {
            deleteOnExit()
        }
    }

    @Before
    fun setUp() {
        queue = LinkedList()
    }

    @After
    fun cleanUp() {
        queue.clear()
    }

    @Test
    fun testProcessGoodFile() {
        val file = getTestFile("en_ulb_mat_verse.tr")
        val result = TrProcessor(directoryProvider).process(file, queue)

        assertEquals(FileStatus.PROCESSED, result?.status)
        assertEquals(0, queue.size)
    }

    @Test
    fun testProcessBadFile() {
        val file = getTestFile("fake.tr")
        val result = TrProcessor(directoryProvider).process(file, queue)

        assertEquals(FileStatus.REJECTED, result?.status)
        assertEquals(0, queue.size)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
                ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}