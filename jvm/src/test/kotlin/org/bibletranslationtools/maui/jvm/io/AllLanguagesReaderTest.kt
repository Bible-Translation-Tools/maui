package org.bibletranslationtools.maui.jvm.io

import org.junit.Assert
import org.junit.Test

class AllLanguagesReaderTest {

    @Test
    fun testReadLanguagesFile() {

        val result = AllLanguagesReader().read()

        Assert.assertTrue(result.isNotEmpty())
    }
}
