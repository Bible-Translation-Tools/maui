package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.VerifiedResult
import org.bibletranslationtools.maui.common.extensions.MediaExtensions
import org.bibletranslationtools.maui.common.io.Versification

class ChapterVerifier(private val versification: Versification) : FileVerifier() {
    override fun verify(media: Media): VerifiedResult {
        val book = media.book?.uppercase()
        val chapter = media.chapter

        return when {
            media.chapter != null -> verifyChapter(book, chapter)
            media.grouping == Grouping.CHAPTER ->
                rejected("File with grouping ${Grouping.CHAPTER} must have chapter.")
            media.grouping == Grouping.CHUNK && media.extension != MediaExtensions.TR ->
                rejected("${media.extension} file with grouping ${Grouping.CHUNK} must have chapter.")
            media.grouping == Grouping.VERSE && media.extension != MediaExtensions.TR ->
                rejected("${media.extension} file with grouping ${Grouping.VERSE} must have chapter.")
            else -> processed()
        }
    }

    private fun verifyChapter(book: String?, chapter: Int?): VerifiedResult {
        val bookData = versification[book] ?: listOf()
        val range = 1..bookData.size

        return when {
            bookData.isEmpty() -> {
                rejected("$chapter is not found in the book $book.")
            }
            !range.contains(chapter) -> {
                rejected("$book only has ${bookData.size} chapters, not $chapter.")
            }
            else -> processed()
        }
    }
}