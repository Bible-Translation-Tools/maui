package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.fileverifier.BookVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class BookVerifierTest {
    private val versification = mapOf("MAT" to listOf(1))
    private val wavFile = "en_ulb_b41_mat_c01.wav"
    private val badFile = "fake.jpg"

    @Test
    fun verificationSuccess() {
        val media = Media(
            file = getTestFile(wavFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            grouping = Grouping.CHAPTER
        )
        val result = BookVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailedOnEmptyBook() {
        val media = Media(getTestFile(badFile))
        val result = BookVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Book should be specified.", result.message)
    }

    @Test
    fun verificationFailedOnInvalidBook() {
        val media = Media(getTestFile(badFile), book = "bad")
        val result = BookVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("bad is not a valid book.", result.message)
    }

    @Test
    fun verificationFailedOnBookGroupingWithChapter() {
        val media = Media(getTestFile(badFile), book = "mat", chapter = 1, grouping = Grouping.BOOK)
        val result = BookVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("File with grouping \"${Grouping.BOOK}\" must not have chapter.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}