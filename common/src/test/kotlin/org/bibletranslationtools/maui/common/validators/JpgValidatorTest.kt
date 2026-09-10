package org.bibletranslationtools.maui.common.validators

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.lang.IllegalArgumentException

class JpgValidatorTest {

    @Test
    fun testGoodJpgFile() {
        val file = File(javaClass.getResource("/test.jpg").file)
        val validator = JpgValidator(file)

        try {
            validator.validate()
        } catch (e: Exception) {
            Assert.fail("Validate threw exception, however it shouldn't.")
        }
    }

    @Test
    fun testBadJpgFile() {
        val file = File(javaClass.getResource("/fake.jpg").file)
        val validator = JpgValidator(file)

        val error = Assert.assertThrows(
            IllegalArgumentException::class.java,
            validator::validate
        )

        Assert.assertEquals(IllegalArgumentException::class.java, error.javaClass)
        Assert.assertEquals("Not a jpg file", error.message)
    }
}
