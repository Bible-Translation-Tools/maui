package org.bibletranslationtools.maui.common.validators

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.lang.Exception
import kotlin.io.path.createTempDirectory as createTempDir

class TrValidatorTest {

    private val directoryProvider = mockk<IDirectoryProvider> {
        every { createTempDirectory(any()) } returns createTempDir("cache").toFile().apply {
            deleteOnExit()
        }
    }

    @Test
    fun testTrFileExtracts() {
        val file = File(javaClass.getResource("/en_ulb_mat_verse.tr").file)
        val validator = TrValidator(directoryProvider, file)

        try {
            validator.validate()
        } catch (e: Exception) {
            Assert.fail("Validate threw exception, however it shouldn't.")
        }
    }

    @Test
    fun testTrFileExtractFails() {
        val file = File(javaClass.getResource("/fake.tr").file)
        val validator = TrValidator(directoryProvider, file)

        val error = Assert.assertThrows(
            IllegalArgumentException::class.java,
            validator::validate
        )

        Assert.assertEquals(IllegalArgumentException::class.java, error.javaClass)
        Assert.assertEquals("TR file is not valid.", error.message)
    }
}
