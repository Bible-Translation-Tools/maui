package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.fileverifier.ContentVerifier
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class ContentVerifierTest {
    private val versification = mapOf("MAT" to listOf(1, 23))
    private val chapterFile = "en_ulb_b41_mat_c01.wav"
    private val verseFile = "en_ulb_b41_mat_c01_v01_t01.wav"
    private val duplicateMarkersFile = "en_mat_c01_duplicate_markers.wav"
    private val duplicateLocationsFile = "en_mat_c01_duplicate_locations.wav"
    private val disorderedMarkersFile = "en_mat_c01_disordered_markers.wav"
    private val badFirstVerseFile = "en_ulb_b41_mat_c01_v100.wav"
    private val badLastVerseFile = "en_ulb_b41_mat_c01_v1-100.wav"
    private val equalVersesFile = "en_ulb_b41_mat_c01_v01-01.wav"
    private val noMarkersFile = "en_ulb_b41_mat_c01_v01.wav"
    private val inconsistentMarkersFile = "en_mat_c01_v02-05.wav"
    private val wrongMarkersSizeFile = "en_mat_c01_v05.wav"
    private val wrongMarkersFile = "en_mat_c01_v03-04.wav"

    @Test
    fun verificationSuccessChapterFile() {
        val media = Media(
            file = getTestFile(chapterFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            grouping = Grouping.CHAPTER
        )
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationSuccessVerseFile() {
        val media = Media(
            file = getTestFile(verseFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            grouping = Grouping.VERSE
        )
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationSuccessOnNoChapter() {
        val media = Media(
            file = getTestFile(verseFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            grouping = Grouping.VERSE
        )
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.PROCESSED, result.status)
        Assert.assertEquals(null, result.message)
    }

    @Test
    fun verificationFailVerseFileWithChapterGrouping() {
        val media = Media(
            file = getTestFile(verseFile),
            language = "en",
            resourceType = "ulb",
            book = "mat",
            chapter = 1,
            grouping = Grouping.CHAPTER
        )
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("File with verses should be verse or chunk grouping.", result.message)
    }

    @Test
    fun verificationFailedOnVerseFileWithNoGrouping() {
        val media = Media(getTestFile(badFirstVerseFile), book = "mat", chapter = 1)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("File with verses should be verse or chunk grouping.", result.message)
    }

    @Test
    fun verificationFailedOnEmptyBook() {
        val media = Media(getTestFile(chapterFile), chapter = 1)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("1 is not found in the book null.", result.message)
    }

    @Test
    fun verificationFailedOnWrongBook() {
        val media = Media(getTestFile(chapterFile), book = "bad", chapter = 1)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("1 is not found in the book BAD.", result.message)
    }

    @Test
    fun verificationFailedOnWrongChapter() {
        val media = Media(getTestFile(chapterFile), book = "mat", chapter = 100)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("100 is not found in the book MAT.", result.message)
    }

    @Test
    fun verificationFailedOnDuplicateMarkers() {
        val media = Media(getTestFile(duplicateMarkersFile), book = "mat", chapter = 1)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("There are duplicate marker labels in the file.", result.message)
    }

    @Test
    fun verificationFailedOnDuplicateLocations() {
        val media = Media(getTestFile(duplicateLocationsFile), book = "mat", chapter = 1)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("There duplicate audio locations in the file.", result.message)
    }

    @Test
    fun verificationFailedOnDisorderedMarkers() {
        val media = Media(getTestFile(disorderedMarkersFile), book = "mat", chapter = 1)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Marker locations and/or labels are not in correct order.", result.message)
    }

    @Test
    fun verificationFailedOnBadFirstVerse() {
        val media = Media(getTestFile(badFirstVerseFile), book = "mat", chapter = 2, grouping = Grouping.CHUNK)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("First verse expected to be in range from 1 to 23, but got 100.", result.message)
    }

    @Test
    fun verificationFailedOnBadLastVerse() {
        val media = Media(getTestFile(badLastVerseFile), book = "mat", chapter = 2, grouping = Grouping.CHUNK)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Last verse expected to be in range from 1 to 23, but got 100.", result.message)
    }

    @Test
    fun verificationFailedOnEqualVerses() {
        val media = Media(getTestFile(equalVersesFile), book = "mat", chapter = 2, grouping = Grouping.CHUNK)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("First verse should not be greater than or equal to last verse.", result.message)
    }

    @Test
    fun verificationFailedOnNoMarkers() {
        val media = Media(getTestFile(noMarkersFile), book = "mat", chapter = 2, grouping = Grouping.CHUNK)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Chunk/Verse file should have at least one verse marker.", result.message)
    }

    @Test
    fun verificationFailedOnInconsistentMarkers() {
        val media = Media(getTestFile(inconsistentMarkersFile), book = "mat", chapter = 2, grouping = Grouping.CHUNK)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Verses in the file name differ from number of markers in metadata.", result.message)
    }

    @Test
    fun verificationFailedOnWrongMarkersSize() {
        val media = Media(getTestFile(wrongMarkersSizeFile), book = "mat", chapter = 2, grouping = Grouping.CHUNK)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("There are 3 markers in metadata. Should be only 1.", result.message)
    }

    @Test
    fun verificationFailedOnWrongMarkers() {
        val media = Media(getTestFile(wrongMarkersFile), book = "mat", chapter = 2, grouping = Grouping.CHUNK)
        val result = ContentVerifier(versification).verify(media)

        Assert.assertEquals(FileStatus.REJECTED, result.status)
        Assert.assertEquals("Verses in the file name differ from the verse markers in metadata.", result.message)
    }

    private fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Test resource not found: $fileName")
        return File(resource.path)
    }
}