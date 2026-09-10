package org.bibletranslationtools.maui.common.data

import org.junit.Assert
import org.junit.Test
import java.io.File

class MediaTest {

    private val mediaTr = Media(File("test.tr"))
    private val mediaMp3 = Media(File("test.mp3"))
    private val mediaWav = Media(File("test.wav"))
    private val mediaTrCompressed = Media(
        file = File("test.tr"),
        mediaExtension = MediaExtension.MP3
    )

    @Test
    fun testMediaIsContainer() {
        Assert.assertTrue(mediaTr.isContainer)
    }

    @Test
    fun testMediaIsCompressed() {
        Assert.assertTrue(mediaMp3.isCompressed)
    }

    @Test
    fun testMediaIsNotCompressed() {
        Assert.assertTrue(!mediaWav.isCompressed)
    }

    @Test
    fun testMediaIsContainerAndCompressed() {
        Assert.assertTrue(mediaTrCompressed.isContainerAndCompressed)
    }
}
