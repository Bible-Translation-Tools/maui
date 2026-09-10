package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.fileverifier.GroupingVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class GroupingVerifierTest {
    private val wavFile = "en_ulb_b41_mat_c01.wav"

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
        val result = GroupingVerifier().verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailed() {
        val media = Media(
            file = getTestFile(wavFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1
        )
        val result = GroupingVerifier().verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Grouping needs to be specified.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}