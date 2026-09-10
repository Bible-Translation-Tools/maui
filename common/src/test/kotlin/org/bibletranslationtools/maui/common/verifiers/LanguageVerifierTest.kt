package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.fileverifier.LanguageVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class LanguageVerifierTest {

    private val validLanguages = listOf("en")
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
        val result = LanguageVerifier(validLanguages).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailedOnEmptyLanguage() {
        val media = Media(getTestFile(badFile))
        val result = LanguageVerifier(validLanguages).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Language should be specified.", result.message)
    }

    @Test
    fun verificationFailedOnInvalidLanguage() {
        val media = Media(getTestFile(badFile), "bad")
        val result = LanguageVerifier(validLanguages).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("bad is not a valid language.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}