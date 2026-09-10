package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.fileverifier.ChapterVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class ChapterVerifierTest {
    private val versification = mapOf("MAT" to listOf(25, 23, 17))
    private val wavFile = "en_ulb_b41_mat_c01.wav"
    private val trFile = "en_ulb_mat_verse.tr"

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
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailedOnNoBookSpecified() {
        val media = Media(getTestFile(wavFile), chapter = 1)
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("1 is not found in the book null.", result.message)
    }

    @Test
    fun verificationFailedOnEmptyChapterWithGroupingChapter() {
        val media = Media(getTestFile(wavFile), grouping = Grouping.CHAPTER)
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("File with grouping chapter must have chapter.", result.message)
    }

    @Test
    fun verificationFailedOnInvalidChapter() {
        val media = Media(getTestFile(wavFile), book = "mat", chapter = 100)
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("MAT only has ${versification["MAT"]?.size} chapters, not 100.", result.message)
    }

    @Test
    fun verificationFailedOnGroupingChunkNoContainer() {
        val media = Media(getTestFile(wavFile), grouping = Grouping.CHUNK)
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("wav file with grouping chunk must have chapter.", result.message)
    }

    @Test
    fun verificationSuccessOnGroupingChunkContainer() {
        val media = Media(getTestFile(trFile), grouping = Grouping.CHUNK)
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailedOnGroupingVerseNoContainer() {
        val media = Media(getTestFile(wavFile), grouping = Grouping.VERSE)
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("wav file with grouping verse must have chapter.", result.message)
    }

    @Test
    fun verificationSuccessOnGroupingVerseContainer() {
        val media = Media(getTestFile(trFile), grouping = Grouping.VERSE)
        val result = ChapterVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}