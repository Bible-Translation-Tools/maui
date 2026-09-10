package org.bibletranslationtools.maui.common.validators

import org.junit.Assert
import org.junit.Test
import java.io.File

class Mp3ValidatorTest {

    @Test
    fun testGoodMp3File() {
        val file = File(javaClass.getResource("/test.mp3").file)
        val validator = Mp3Validator(file)

        try {
            validator.validate()
        } catch (e: Exception) {
            Assert.fail("Validate threw exception, however it shouldn't.")
        }
    }

    @Test
    fun testBadMp3File() {
        val file = File(javaClass.getResource("/fake.mp3").file)
        val validator = Mp3Validator(file)

        val error = Assert.assertThrows(
            IllegalArgumentException::class.java,
            validator::validate
        )

        Assert.assertEquals(IllegalArgumentException::class.java, error.javaClass)
        Assert.assertEquals("Not a mp3 file", error.message)
    }
}
