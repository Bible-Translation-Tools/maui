package org.bibletranslationtools.maui.common.fileverifier

/**
 * Recognizes Orature cue labels:
 * - verse: "1" or "orature-vm-1"
 * - book title: "orature-book-jhn"
 * - chapter title: "orature-chapter-1"
 */
internal object MarkerLabel {
    private val VERSE = Regex("""^(?:orature-vm-)?(\d+)$""", RegexOption.IGNORE_CASE)
    private val BOOK_TITLE = Regex("""^orature-book-([a-zA-Z0-9]+)$""", RegexOption.IGNORE_CASE)
    private val CHAPTER_TITLE = Regex("""^orature-chapter-(\d+)$""", RegexOption.IGNORE_CASE)

    fun isVerse(label: String): Boolean = verseNumber(label) != null

    fun isTitle(label: String): Boolean =
        isBookTitle(label) || isChapterTitle(label)

    fun isBookTitle(label: String): Boolean =
        BOOK_TITLE.matches(label.trim())

    fun isChapterTitle(label: String): Boolean =
        CHAPTER_TITLE.matches(label.trim())

    fun verseNumber(label: String): Int? =
        VERSE.matchEntire(label.trim())?.groupValues?.get(1)?.toIntOrNull()

    fun bookSlug(label: String): String? =
        BOOK_TITLE.matchEntire(label.trim())?.groupValues?.get(1)?.lowercase()

    fun chapterNumber(label: String): Int? =
        CHAPTER_TITLE.matchEntire(label.trim())?.groupValues?.get(1)?.toIntOrNull()
}
