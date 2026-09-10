package org.bibletranslationtools.maui.common.validators

import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.audio.wav.InvalidWavFileException
import java.io.File

class WavValidatorTest {

    @Test
    fun testGoodWavFile() {
        val file = File(javaClass.getResource("/en_ulb_b41_mat_c01.wav").file)
        val validator = WavValidator(file)

        try {
            validator.validate()
        } catch (e: Exception) {
            Assert.fail("Validate threw exception, however it shouldn't.")
        }
    }

    @Test
    fun testBadWavFile() {
        val file = File(javaClass.getResource("/fake.wav").file)
        val validator = WavValidator(file)

        val error = Assert.assertThrows(
            InvalidWavFileException::class.java,
            validator::validate
        )

        Assert.assertEquals(InvalidWavFileException::class.java, error.javaClass)
        Assert.assertEquals("File does not contain a RIFF header.", error.message)
    }

    @Test
    fun testWavChunkHasGoodMetadata() {
        val file = File(javaClass.getResource("/en_ulb_b41_mat_c01_v01_t01.wav").file)
        val validator = WavValidator(file)

        try {
            validator.validate()
        } catch (e: Exception) {
            Assert.fail("Validate threw exception, however it shouldn't.")
        }
    }

    @Test
    fun testWavChunkHasBadMetadata() {
        val file = File(javaClass.getResource("/en_ulb_b41_mat_c01_v01_t01_bad.wav").file)
        val validator = WavValidator(file)

        val error = Assert.assertThrows(
            InvalidWavFileException::class.java,
            validator::validate
        )

        Assert.assertEquals(InvalidWavFileException::class.java, error.javaClass)
        Assert.assertEquals("Chunk has corrupt metadata", error.message)
    }

    @Test
    fun testWavWithCustomExtension() {
        val file = File(javaClass.getResource("/wav_with_custom_extension.wav").file)
        val validator = WavValidator(file)

        val error = Assert.assertThrows(
            InvalidWavFileException::class.java,
            validator::validate
        )

        Assert.assertEquals(InvalidWavFileException::class.java, error.javaClass)
        Assert.assertEquals("wav file with custom extension is not supported", error.message)
    }
}
