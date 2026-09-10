package org.bibletranslationtools.maui.jvm.io

import org.junit.Assert
import org.junit.Test

class VersificationReaderTest {

    @Test
    fun testReadVersificationFile() {

        val result = VersificationReader().read()

        Assert.assertTrue(result.isNotEmpty())
        Assert.assertEquals(50, result["GEN"]?.size)
        Assert.assertEquals(150, result["PSA"]?.size)
    }
}