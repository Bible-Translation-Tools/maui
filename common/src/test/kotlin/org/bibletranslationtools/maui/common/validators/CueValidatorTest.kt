package org.bibletranslationtools.maui.common.validators

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File

class CueValidatorTest {

    @Rule
    @JvmField
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun testGoodPlainTextFile() {
        val file = File(javaClass.getResource("/test.cue").file)
        val validator = CueValidator(file)

        try {
            validator.validate()
        } catch (e: Exception) {
            Assert.fail("Validate threw exception, however it shouldn't.")
        }
    }

    @Test
    fun testBadPlainTextFile() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("This doesn't look like a CUE file")

        val file = File(javaClass.getResource("/fake.cue").file)
        val validator = CueValidator(file)
        validator.validate()
    }
}
