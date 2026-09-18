package org.bibletranslationtools.maui.common.usecases

import org.bibletranslationtools.maui.common.data.Media
import org.junit.Assert
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempFile

class ExportCsvTest {

    @Test
    fun exportTest() {
        val output = createTempFile("temp").toFile().apply {
            deleteOnExit()
        }
        val preSize = output.length()
        val secondFile = File("/home/user/test.mp3")
        val items = listOf(
            Media(File("example.wav"), selected = true),
            Media(secondFile, "en", "ulb", "gen")
        )
        val result = ExportCsv().export(items, output).test()
        val postSize = output.length()

        result.assertComplete()
        result.assertNoErrors()

        Assert.assertNotEquals(preSize, postSize)

        val expectedHeader = "selected,file name,parent file,language,resource type,book,chapter,media extension," +
                "media quality,grouping,status,status message"
        val expectedFirstMedia = "*,\"example.wav\",\"--\",--,--,--,--,--,--,--,--,--"
        val expectedSecondMedia = ",\"$secondFile\",\"--\",en,ulb,gen,--,--,--,--,--,--"

        var line = 1
        output.forEachLine {
            when (line) {
                1 -> Assert.assertEquals(expectedHeader, it)
                2 -> Assert.assertEquals(expectedFirstMedia, it)
                3 -> Assert.assertEquals(expectedSecondMedia, it)
            }
            line++
        }
    }
}