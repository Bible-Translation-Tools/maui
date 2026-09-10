package org.bibletranslationtools.maui.common.validators

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class OratureValidatorTest {
    private val oratureFileName = "orature_file.zip"

    @Test
    fun testGoodOratureZipFile() {
        val file = getOratureFile()

        try {
            OratureValidator(file).validate()
        } catch (e: Exception) {
            Assert.fail("Validate threw exception, however it shouldn't.")
        }
    }

    @Test
    fun testBadOratureZipFile() {
        val fakeZip = createTempFile(suffix = ".zip").apply { deleteOnExit() }

        try {
            OratureValidator(fakeZip).validate()
        } catch (e: Exception) {
            Assert.assertNotNull(e)
        }
    }

    private fun getOratureFile(): File {
        val resource = javaClass.classLoader.getResource(oratureFileName)
                ?: throw FileNotFoundException("Test resource not found: $oratureFileName")
        return File(resource.path)
    }
}