package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.*
import org.bibletranslationtools.maui.common.fileverifier.MediaQualityVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class MediaQualityVerifierTest {
    private val wavFile = "en_ulb_b41_mat_c01.wav"
    private val mp3File = "test.mp3"
    private val trFile = "en_ulb_mat_verse.tr"

    @Test
    fun verifiedSuccessOnNonCompressed() {
        val media = Media(
            file = getTestFile(wavFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            grouping = Grouping.CHAPTER
        )
        val result = MediaQualityVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verifiedSuccessOnCompressed() {
        val media = Media(
            file = getTestFile(mp3File),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            mediaQuality = MediaQuality.HI,
            grouping = Grouping.CHAPTER
        )
        val result = MediaQualityVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verifiedSuccessOnCompressedContainer() {
        val media = Media(
            getTestFile(trFile),
            mediaExtension = MediaExtension.MP3,
            mediaQuality = MediaQuality.HI
        )
        val result = MediaQualityVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verifiedSuccessOnNonCompressedContainer() {
        val media = Media(getTestFile(trFile), mediaExtension = MediaExtension.WAV)
        val result = MediaQualityVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailedOnCompressedWithEmptyMediaQuality() {
        val media = Media(getTestFile(mp3File))
        val result = MediaQualityVerifier().verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Media quality needs to be specified for compressed media.", result.message)
    }

    @Test
    fun verificationFailedOnCompressedContainerWithEmptyMediaQuality() {
        val media = Media(getTestFile(trFile), mediaExtension = MediaExtension.MP3)
        val result = MediaQualityVerifier().verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Media quality needs to be specified for compressed media.", result.message)
    }

    @Test
    fun verificationFailedOnNonCompressedWithMediaQuality() {
        val media = Media(getTestFile(wavFile), mediaQuality = MediaQuality.HI)
        val result = MediaQualityVerifier().verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Non-compressed media should not have a quality.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}