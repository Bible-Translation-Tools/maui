package org.bibletranslationtools.maui.common.usecases

import io.reactivex.Single
import org.bibletranslationtools.maui.common.data.Media
import java.util.regex.Pattern

class MakePath(private val media: Media) {

    companion object {
        private const val CONTENTS = "CONTENTS"
    }

    private val filename = normalizeFileName()

    fun build(): Single<String> {
        return Single.fromCallable {
            val initialPath = buildInitialPath()

            when {
                media.isContainerAndCompressed -> {
                    arrayOf(
                        initialPath,
                        media.mediaExtension,
                        media.mediaQuality,
                        media.grouping,
                        filename
                    ).joinToString("/")
                }
                media.isContainer -> {
                    arrayOf(
                        initialPath,
                        media.mediaExtension,
                        media.grouping,
                        filename
                    ).joinToString("/")
                }
                media.isCompressed -> {
                    arrayOf(
                        initialPath,
                        media.mediaQuality,
                        media.grouping,
                        filename
                    ).joinToString("/")
                }
                else -> {
                    arrayOf(
                        initialPath,
                        media.grouping,
                        filename
                    ).joinToString("/")
                }
            }
        }
    }

    private fun buildInitialPath(): String {
        return when {
            media.chapter != null -> {
                arrayOf(
                    media.language,
                    media.resourceType,
                    media.book,
                    media.chapter,
                    CONTENTS,
                    media.extension
                ).joinToString("/")
            }
            else -> {
                arrayOf(
                    media.language,
                    media.resourceType,
                    media.book,
                    CONTENTS,
                    media.extension
                ).joinToString("/")
            }
        }
    }

    private fun normalizeFileName(): String {
        val str = StringBuilder()
        str.append("${media.language}_${media.resourceType}_${media.book}")

        media.chapter?.let { chapter ->
            val verse = getVerse()

            // Different chapter padding for verse files and chapter files
            if (verse == null) {
                str.append("_c$chapter")
            } else {
                str.append("_c${padChapter(media.book, chapter)}")
                str.append("_$verse")
            }
        }

        str.append(".${media.extension}")

        return str.toString()
    }

    private fun padChapter(book: String?, chapter: Int): String {
        return if (book == "psa") {
            chapter.toString().padStart(3, '0')
        } else {
            chapter.toString().padStart(2, '0')
        }
    }

    private fun getVerse(): String? {
        val verseRegex = "_(v\\d{1,3}(-\\d{1,3})?)"
        val pattern = Pattern.compile(verseRegex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(media.file.nameWithoutExtension)

        val found = matcher.find()
        return if (found) {
            matcher.group(1)?.lowercase()
        } else null
    }
}
