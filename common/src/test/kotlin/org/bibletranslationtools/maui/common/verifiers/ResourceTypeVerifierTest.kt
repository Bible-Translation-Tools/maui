package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.fileverifier.ResourceTypeVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class ResourceTypeVerifierTest {

    private val validResourceTypes = listOf("ulb")
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
        val result = ResourceTypeVerifier(validResourceTypes).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailedOnEmptyResourceType() {
        val media = Media(getTestFile(badFile))
        val result = ResourceTypeVerifier(validResourceTypes).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Resource type should be specified.", result.message)
    }

    @Test
    fun verificationFailedOnInvalidResourceType() {
        val media = Media(getTestFile(badFile), resourceType = "bad")
        val result = ResourceTypeVerifier(validResourceTypes).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("bad is not a valid resource type.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}