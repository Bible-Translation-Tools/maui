package org.bibletranslationtools.maui.jvm.io

import org.junit.Assert
import org.junit.Test

class BooksReaderTest {

    @Test
    fun testReadBooksFile() {
        val result = BooksReader().read()

        Assert.assertEquals(66, result.size)
    }
}
