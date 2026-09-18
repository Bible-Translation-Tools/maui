package org.bibletranslationtools.maui.common.verifiers

import org.bibletranslationtools.maui.common.fileverifier.MarkerLabel
import org.junit.Assert
import org.junit.Test

class MarkerLabelTest {

    @Test
    fun parsesPlainVerseLabels() {
        Assert.assertEquals(1, MarkerLabel.verseNumber("1"))
        Assert.assertEquals(12, MarkerLabel.verseNumber("12"))
        Assert.assertTrue(MarkerLabel.isVerse("1"))
    }

    @Test
    fun parsesOratureVerseLabels() {
        Assert.assertEquals(1, MarkerLabel.verseNumber("orature-vm-1"))
        Assert.assertEquals(23, MarkerLabel.verseNumber("orature-vm-23"))
        Assert.assertTrue(MarkerLabel.isVerse("orature-vm-3"))
    }

    @Test
    fun parsesBookTitleLabels() {
        Assert.assertTrue(MarkerLabel.isBookTitle("orature-book-jhn"))
        Assert.assertTrue(MarkerLabel.isTitle("orature-book-jhn"))
        Assert.assertEquals("jhn", MarkerLabel.bookSlug("orature-book-jhn"))
        Assert.assertFalse(MarkerLabel.isVerse("orature-book-jhn"))
        Assert.assertNull(MarkerLabel.verseNumber("orature-book-jhn"))
    }

    @Test
    fun parsesChapterTitleLabels() {
        Assert.assertTrue(MarkerLabel.isChapterTitle("orature-chapter-1"))
        Assert.assertTrue(MarkerLabel.isTitle("orature-chapter-1"))
        Assert.assertEquals(1, MarkerLabel.chapterNumber("orature-chapter-1"))
        Assert.assertFalse(MarkerLabel.isVerse("orature-chapter-1"))
        Assert.assertNull(MarkerLabel.verseNumber("orature-chapter-1"))
    }

    @Test
    fun rejectsUnknownLabelsAsVerses() {
        Assert.assertNull(MarkerLabel.verseNumber("intro"))
        Assert.assertNull(MarkerLabel.verseNumber("orature-foo-1"))
        Assert.assertFalse(MarkerLabel.isTitle("orature-vm-1"))
    }
}
