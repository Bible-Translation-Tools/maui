package org.bibletranslationtools.common.usecases

import io.reactivex.Single
import org.bibletranslationtools.common.data.FileData
import org.bibletranslationtools.common.data.Grouping
import org.bibletranslationtools.common.data.MediaQuality
import org.bibletranslationtools.common.data.ResourceType
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

class ParseFileName(private val file: File) {

    private enum class GROUPS(val value: Int) {
        LANGUAGE(1),
        RESOURCE_TYPE(2),
        BOOK_NUMBER(3),
        BOOK_SLUG(4),
        CHAPTER(5),
        FIRST_VERSE(6),
        LAST_VERSE(7),
        TAKE(8),
        QUALITY(9),
        GROUPING(10)
    }

    companion object {
        private const val LANGUAGE = "([a-zA-Z]{2,3}[-\\w+]*?)"
        private const val RESOURCE_TYPE = "(?:_([a-zA-Z]{3}))"
        private const val BOOK_NUMBER = "(?:_b([\\d]{2}))?"
        private const val BOOK = "(?:_([1-3]{0,1}[a-zA-Z]{2,3}))"
        private const val CHAPTER = "(?:_c([\\d]{1,3}))?"
        private const val VERSE = "(?:_v([\\d]{1,3})(?:-([\\d]{1,3}))?)?"
        private const val TAKE = "(?:_t([\\d]{1,2}))?"
        private const val QUALITY = "(?:_(hi|low))?"
        private const val GROUPING = "(?:_(book|chapter|chunk|verse))?"
        private const val FILENAME_PATTERN = LANGUAGE +
                RESOURCE_TYPE +
                BOOK_NUMBER +
                BOOK +
                CHAPTER +
                VERSE +
                TAKE +
                QUALITY +
                GROUPING

    }

    private var matcher: Matcher? = null

    init {
        matcher = findMatch(file.nameWithoutExtension)
    }

    fun parse(): Single<FileData> {
        return Single.fromCallable {
            val fileData = FileData(file)

            fileData.language = findLanguage()
            fileData.resourceType = findResourceType()
            fileData.book = findBook()
            fileData.chapter = findChapter()
            fileData.mediaQuality = findQuality()
            fileData.grouping = findGrouping()

            fileData
        }
    }

    private fun findMatch(fileName: String): Matcher? {
        val pattern = Pattern.compile(FILENAME_PATTERN, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(fileName)

        val found = matcher.find()

        return if (found) matcher else null
    }

    private fun findLanguage(): String? {
        return matcher?.let { _matcher ->
            _matcher.group(GROUPS.LANGUAGE.value)
        }
    }

    private fun findResourceType(): ResourceType? {
        return matcher?.let { _matcher ->
            val resourceType = _matcher.group(GROUPS.RESOURCE_TYPE.value)
            resourceType?.let {
                ResourceType.of(it)
            }
        }
    }

    private fun findBook(): String? {
        return matcher?.let { _matcher ->
            _matcher.group(GROUPS.BOOK_SLUG.value)
        }
    }

    private fun findChapter(): Int? {
        return matcher?.let { _matcher ->
            _matcher.group(GROUPS.CHAPTER.value)?.let {
                it.toInt()
            }
        }
    }

    private fun findGrouping(): Grouping? {
        return matcher?.let { _matcher ->
            when {
                _matcher.group(GROUPS.GROUPING.value) != null ->
                    Grouping.of(_matcher.group(GROUPS.GROUPING.value))
                _matcher.group(GROUPS.LAST_VERSE.value) != null ->
                    Grouping.of("chunk")
                _matcher.group(GROUPS.FIRST_VERSE.value) != null ->
                    Grouping.of("verse")
                else -> null
            }
        }
    }

    private fun findQuality(): MediaQuality? {
        return matcher?.let { _matcher ->
            val quality = _matcher.group(GROUPS.QUALITY.value)
            quality?.let {
                MediaQuality.of(it)
            }
        }
    }
}
