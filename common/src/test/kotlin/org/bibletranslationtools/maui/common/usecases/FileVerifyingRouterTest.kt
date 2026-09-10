package org.bibletranslationtools.maui.common.usecases

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.io.*
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class FileVerifyingRouterTest {
    private val languagesReader = mockk<ILanguagesReader> {
        every { read() } returns listOf("en")
    }
    private val resourceTypesReader = mockk<IResourceTypesReader> {
        every { read() } returns listOf("ulb")
    }
    private val versificationReader = mockk<IVersificationReader> {
        every { read() } returns mapOf("MAT" to listOf(1))
    }

    private val wavFile = "en_ulb_b41_mat_c01.wav"
    private val badFile = "fake.jpg"

    @Test
    fun testHandleMedia() {
        val media = Media(
            file = getTestFile(wavFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            grouping = Grouping.CHAPTER
        )
        val result = FileVerifyingRouter(
            versificationReader,
            languagesReader,
            resourceTypesReader
        ).handleMedia(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun testHandleBadMedia() {
        val media = Media(
            file = getTestFile(badFile),
            grouping = Grouping.CHAPTER
        )
        val result = FileVerifyingRouter(
            versificationReader,
            languagesReader,
            resourceTypesReader
        ).handleMedia(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Language should be specified.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}