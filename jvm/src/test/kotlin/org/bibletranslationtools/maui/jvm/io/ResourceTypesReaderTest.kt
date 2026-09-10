package org.bibletranslationtools.maui.jvm.io

import org.junit.Assert
import org.junit.Test

class ResourceTypesReaderTest {

    @Test
    fun testReadResourceTypesFile() {
        val result = ResourceTypesReader().read()

        Assert.assertTrue(result.contains("ulb"))
        Assert.assertTrue(result.contains("reg"))
    }
}
