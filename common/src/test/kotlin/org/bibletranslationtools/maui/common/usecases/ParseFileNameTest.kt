package org.bibletranslationtools.maui.common.usecases

import org.bibletranslationtools.maui.common.data.FileStatus
import org.junit.Assert.assertEquals
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.MediaQuality
import org.junit.Test
import java.io.File

class ParseFileNameTest {

    @Test
    fun parseFileNameWithValidInfo() {
        val file = File("en_ot_ulb_b01_gen_c01_v01_t01.wav")
        val expected = Media(
            file,
            "en",
            "ulb",
            "gen",
            1,
            null,
            null,
            null,
            FileStatus.PROCESSED
        )
        val result = ParseFileName(file).parse()

        assertEquals(expected, result)
    }

    @Test
    fun parseFileNameWithInvalidInfo() {
        val file = File("test.wav")
        val expected = Media(file, status = FileStatus.PROCESSED)
        val result = ParseFileName(file).parse()

        assertEquals(expected, result)
    }

    @Test
    fun parseFileNameUpperCaseWithValidInfo() {
        val file = File("EN_ULB_B01_GEN_C01_V01_T01.wav")
        val expected = Media(
            file,
            "en",
            "ulb",
            "gen",
            1,
            null,
            null,
            null,
            FileStatus.PROCESSED
        )
        val result = ParseFileName(file).parse()

        assertEquals(expected, result)
    }

    @Test
    fun mediaHasLanguage() {
        val file = File("en_ot_ulb_b01_gen_c01_v01_t01.wav")
        val expected = Media(
            file,
            "en",
            "ulb",
            "gen",
            1,
            null,
            null,
            Grouping.VERSE
        )
        val result = ParseFileName(file).parse()

        assertEquals(expected.language, result.language)
    }

    @Test
    fun mediaLanguageNullWithInvalidFileName() {
        val file = File("test.wav")
        val expected = Media(file)
        val result = ParseFileName(file).parse()

        assertEquals(expected.language, result.language)
    }

    @Test
    fun mediaHasGrouping() {
        val file = File("en_ulb_gen_c02_chunk.tr")
        val expected = Media(
            file,
            "en",
            "ulb",
            "gen",
            2,
            null,
            null,
            Grouping.CHUNK
        )
        val result = ParseFileName(file).parse()

        assertEquals(expected.grouping, result.grouping)
    }

    @Test
    fun mediaHasMediaQuality() {
        val file = File("en_ulb_gen_low_verse.mp3")
        val expected = Media(
            file,
            "en",
            "ulb",
            "gen",
            null,
            null,
            MediaQuality.LOW,
            Grouping.VERSE
        )
        val result = ParseFileName(file).parse()

        assertEquals(expected.mediaQuality, result.mediaQuality)
    }
}
