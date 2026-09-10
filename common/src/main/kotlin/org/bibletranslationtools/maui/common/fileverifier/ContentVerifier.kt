package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.VerifiedResult
import org.bibletranslationtools.maui.common.extensions.MediaExtensions
import org.bibletranslationtools.maui.common.io.Versification
import org.digitalmediaserver.cuelib.CueParser
import org.wycliffeassociates.otter.common.audio.AudioCue
import org.wycliffeassociates.otter.common.audio.AudioFile
import org.wycliffeassociates.otter.common.audio.DEFAULT_SAMPLE_RATE
import java.io.File
import java.util.regex.Pattern
import kotlin.math.roundToInt

private const val CUE_FRAME_SIZE = 75.0
private const val DEFAULT_CUE_TRACK_INDEX = 1

class ContentVerifier(private val versification: Versification) : FileVerifier() {

    override fun verify(media: Media): VerifiedResult {

        val verses = getVerses(media.file)
        val wrongGrouping = (verses.first != null || verses.second != null) &&
                (media.grouping != Grouping.VERSE && media.grouping != Grouping.CHUNK)

        return when {
            media.chapter == null -> processed()
            verses.first == null && verses.second == null -> {
                verifyChapter(media)
            }
            wrongGrouping -> {
                rejected("File with verses should be ${Grouping.VERSE} or ${Grouping.CHUNK} grouping.")
            }
            else -> verifyChunk(media, verses.first, verses.second)
        }
    }

    private fun verifyChapter(media: Media): VerifiedResult {
        val book = media.book?.uppercase()
        val chapter = media.chapter
        val extension = media.extension

        return when (extension) {
            MediaExtensions.WAV -> {
                val cues = parseWav(media.file)
                verifyCues(cues).let {
                    if (it.status == FileStatus.PROCESSED) {
                        verifyChapterVerses(book, chapter, cues)
                    } else it
                }
            }
            MediaExtensions.CUE -> {
                val cues = parseCue(media.file)
                verifyCues(cues).let {
                    if (it.status == FileStatus.PROCESSED) {
                        verifyChapterVerses(book, chapter, cues)
                    } else it
                }
            }
            else -> processed()
        }
    }

    private fun verifyChunk(media: Media, firstVerse: Int?, lastVerse: Int?): VerifiedResult {
        val book = media.book?.uppercase()
        val chapter = media.chapter
        val extension = media.extension

        return when (extension) {
            MediaExtensions.WAV -> {
                val cues = parseWav(media.file)
                verifyCues(cues).let {
                    if (it.status == FileStatus.PROCESSED) {
                        verifyChunkVerses(book, chapter, firstVerse, lastVerse, cues)
                    } else it
                }
            }
            MediaExtensions.CUE -> {
                val cues = parseCue(media.file)
                verifyCues(cues).let {
                    if (it.status == FileStatus.PROCESSED) {
                        verifyChunkVerses(book, chapter, firstVerse, lastVerse, cues)
                    } else it
                }
            }
            MediaExtensions.MP3, MediaExtensions.JPG -> {
                verifyChunkVerses(book, chapter, firstVerse, lastVerse, null)
            }
            else -> processed()
        }
    }

    private fun verifyCues(cues: List<AudioCue>): VerifiedResult {
        val duplicateLocations = cues.groupingBy { it.location }.eachCount().any { it.value > 1 }
        val duplicateVerses = cues.groupingBy { it.label }.eachCount().any { it.value > 1 }

        // Sort cues by digitized marker labels
        val cueVerses = cues
            .mapNotNull {  cue ->
                cue.label.toIntOrNull()?.let { verse ->
                    Pair(
                        cue.location,
                        verse
                    )
                }
            }
            .sortedBy { it.second }

        return when {
            duplicateLocations -> rejected("There duplicate audio locations in the file.")
            duplicateVerses -> rejected("There are duplicate marker labels in the file.")
            // Check if locations are still sorted correctly
            !cueVerses.zipWithNext { a, b -> a.first <= b.first }.all { it } -> {
                rejected("Marker locations and/or labels are not in correct order.")
            }
            else -> processed()
        }
    }

    private fun verifyChapterVerses(book: String?, chapter: Int?, cues: List<AudioCue>): VerifiedResult {
        val bookData = versification[book]
        val chapterVerses = bookData?.let { chapters ->
            chapter?.let {
                if (chapters.indices.contains(it - 1)) {
                    chapters[it - 1]
                } else null
            }
        } ?: 0

        return when {
            bookData == null || chapterVerses == 0 -> {
                rejected("$chapter is not found in the book $book.")
            }
            cues.size != chapterVerses -> {
                rejected("$book $chapter expected $chapterVerses verses, but got ${cues.size}.")
            }
            else -> processed()
        }
    }

    private fun verifyChunkVerses(
        book: String?,
        chapter: Int?,
        firstVerse: Int?,
        lastVerse: Int?,
        cues: List<AudioCue>?
    ): VerifiedResult {
        val bookData = versification[book]
        val chapterVerses = bookData?.let { chapters ->
            chapter?.let {
                if (chapters.indices.contains(it - 1)) {
                    chapters[it - 1]
                } else null
            }
        } ?: 0
        val range = 1..chapterVerses

        return when {
            bookData == null || chapterVerses == 0 -> {
                rejected("$chapter is not found in the book $book.")
            }
            firstVerse == null && lastVerse == null -> {
                rejected("Chunk/Verse file should have at least one verse in its file name.")
            }
            firstVerse != null && !range.contains(firstVerse) -> {
                rejected("First verse expected to be in range from 1 to $chapterVerses, but got $firstVerse.")
            }
            lastVerse != null && !range.contains(lastVerse) -> {
                rejected("Last verse expected to be in range from 1 to $chapterVerses, but got $lastVerse.")
            }
            (firstVerse != null && lastVerse != null) && firstVerse >= lastVerse -> {
                rejected("First verse should not be greater than or equal to last verse.")
            }
            cues != null && cues.isEmpty() -> {
                rejected("Chunk/Verse file should have at least one verse marker.")
            }
            (cues != null && firstVerse != null && lastVerse != null) &&
                    cues.size != (lastVerse - firstVerse + 1) -> {
                rejected("Verses in the file name differ from number of markers in metadata.")
            }
            (cues != null && firstVerse != null && lastVerse == null) && cues.size > 1 -> {
                rejected("There are ${cues.size} markers in metadata. Should be only 1.")
            }
            cues != null && !hasValidVerses(cues, firstVerse, lastVerse) -> {
                rejected("Verses in the file name differ from the verse markers in metadata.")
            }
            else -> processed()
        }
    }

    private fun hasValidVerses(cues: List<AudioCue>, firstVerse: Int?, lastVerse: Int?): Boolean {
        val cueVerses = cues.mapNotNull { cue ->
            cue.label.toIntOrNull()
        }

        return when {
            firstVerse != null && lastVerse != null -> {
                val filenameVerses = (firstVerse..lastVerse).toList()
                filenameVerses == cueVerses
            }
            firstVerse != null && lastVerse == null -> {
                cueVerses.contains(firstVerse)
            }
            else -> true
        }
    }

    private fun getVerses(file: File) : Pair<Int?, Int?> {
        val pattern = Pattern.compile("_v(\\d{1,3})(?:-(\\d{1,3}))?", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(file.nameWithoutExtension)

        val found = matcher.find()

        return if (found) {
            Pair(
                matcher.group(1)?.toInt(),
                matcher.group(2)?.toInt()
            )
        } else Pair(null, null)
    }

    private fun parseWav(file: File): List<AudioCue> {
        if (file.extension != MediaExtensions.WAV.toString()) {
            throw IllegalArgumentException("File should have ${MediaExtensions.WAV} extension.")
        }

        val audio = AudioFile(file)
        val cues = audio.metadata.getCues()

        return cues
    }

    private fun parseCue(file: File): List<AudioCue> {
        if (file.extension != MediaExtensions.CUE.toString()) {
            throw IllegalArgumentException("File should have ${MediaExtensions.CUE} extension.")
        }

        val cues = mutableListOf<AudioCue>()

        val cueSheet = CueParser.parse(file, Charsets.UTF_8)
        cueSheet.allTrackData.forEach {
            val label = it.title
            val index = it.indices.find { i -> i.number == DEFAULT_CUE_TRACK_INDEX }
            index?.let {
                val position = (index.position.totalFrames / CUE_FRAME_SIZE * DEFAULT_SAMPLE_RATE.toFloat())
                    .roundToInt()
                cues.add(AudioCue(position, label))
            }
        }

        return cues
    }
}