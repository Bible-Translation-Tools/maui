package org.bibletranslationtools.maui.common.usecases

import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.MediaExtension
import org.bibletranslationtools.maui.common.data.MediaQuality
import org.junit.Test
import java.io.File

class MakePathTest {

    @Test
    fun testCompressedMedia() {
        val expected = "en/ulb/gen/1/CONTENTS/mp3/low/verse/en_ulb_gen_c01_v03.mp3"
        val media = Media(
            File("en_ulb_gen_c01_v03_t01.mp3"),
            "en",
            "ulb",
            "gen",
            1,
            null,
            MediaQuality.LOW,
            Grouping.VERSE
        )

        val result = MakePath(media).build().test()

        result.assertComplete()
        result.assertNoErrors()
        result.assertValue(expected)
    }

    @Test
    fun testUncompressedMedia() {
        val expected = "en/ulb/gen/1/CONTENTS/wav/chunk/en_ulb_gen_c01_v03-05.wav"
        val media = Media(
            File("en_ulb_gen_c01_v03-05_t01.wav"),
            "en",
            "ulb",
            "gen",
            1,
            null,
            null,
            Grouping.CHUNK
        )

        val result = MakePath(media).build().test()

        result.assertComplete()
        result.assertNoErrors()
        result.assertValue(expected)
    }

    @Test
    fun testChapterFile() {
        val expected = "en/ulb/gen/1/CONTENTS/mp3/low/chapter/en_ulb_gen_c1.mp3"
        val media = Media(
            File("en_ulb_gen_c01.mp3"),
            "en",
            "ulb",
            "gen",
            1,
            null,
            MediaQuality.LOW,
            Grouping.CHAPTER
        )

        val result = MakePath(media).build().test()

        result.assertComplete()
        result.assertNoErrors()
        result.assertValue(expected)
    }

    @Test
    fun testContainerWithCompressedMedia() {
        val expected = "en/ulb/gen/CONTENTS/tr/mp3/hi/verse/en_ulb_gen.tr"
        val media = Media(
            File("en_ulb_gen_verse.tr"),
            "en",
            "ulb",
            "gen",
            null,
            MediaExtension.MP3,
            MediaQuality.HI,
            Grouping.VERSE
        )

        val result = MakePath(media).build().test()

        result.assertComplete()
        result.assertNoErrors()
        result.assertValue(expected)
    }

    @Test
    fun testJpegNormalized() {
        val expected = "en/ulb/gen/CONTENTS/jpg/low/book/en_ulb_gen.jpg"
        val media = Media(
            File("en_ulb_gen.jpeg"),
            "en",
            "ulb",
            "gen",
            null,
            null,
            MediaQuality.LOW,
            Grouping.BOOK
        )

        val result = MakePath(media).build().test()

        result.assertComplete()
        result.assertNoErrors()
        result.assertValue(expected)
    }
}
