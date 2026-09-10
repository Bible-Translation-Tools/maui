package org.bibletranslationtools.maui.common.fileprocessor

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.ProcessFile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class Mp3ProcessorTest {
    private lateinit var queue: Queue<ProcessFile>

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
        val file = getTestFile("test.mp3")
        val result = Mp3Processor().process(file, queue)

        assertEquals(FileStatus.PROCESSED, result?.status)
        assertEquals(0, queue.size)
    }

    @Test
    fun testProcessBadFile() {
        val file = getTestFile("fake.mp3")
        val result = Mp3Processor().process(file, queue)

        assertEquals(FileStatus.REJECTED, result?.status)
        assertEquals(0, queue.size)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
                ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}