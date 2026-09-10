package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.MediaExtension
import org.bibletranslationtools.maui.common.fileverifier.MediaExtensionVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class MediaExtensionVerifierTest {
    private val wavFile = "en_ulb_b41_mat_c01.wav"
    private val trFile = "en_ulb_mat_verse.tr"

    @Test
    fun verifiedSuccessOnNonContainer() {
        val media = Media(
            file = getTestFile(wavFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            grouping = Grouping.CHAPTER
        )
        val result = MediaExtensionVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verifiedSuccessOnCompressedContainer() {
        val media = Media(getTestFile(trFile), mediaExtension = MediaExtension.MP3)
        val result = MediaExtensionVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verifiedSuccessOnNonCompressedContainer() {
        val media = Media(getTestFile(trFile), mediaExtension = MediaExtension.WAV)
        val result = MediaExtensionVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailedOnContainerWithEmptyMediaExtension() {
        val media = Media(getTestFile(trFile))
        val result = MediaExtensionVerifier().verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Media extension needs to be specified for container.", result.message)
    }

    @Test
    fun verificationFailedOnNonContainerWithMediaExtension() {
        val media = Media(getTestFile(wavFile), mediaExtension = MediaExtension.WAV)
        val result = MediaExtensionVerifier().verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Media extension cannot be applied to non-container media.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}