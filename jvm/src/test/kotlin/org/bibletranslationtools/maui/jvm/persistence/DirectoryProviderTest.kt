package org.bibletranslationtools.maui.jvm.persistence

import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class DirectoryProviderTest {

    companion object {
        private val directoryProvider = DirectoryProvider("maui_test")

        @JvmStatic
        @AfterClass
        fun tearDown() {
            directoryProvider.getAppDataDirectory().deleteRecursively()
        }
    }

    @Before
    fun beforeTest() {
        directoryProvider.cleanTempDirectory()
        directoryProvider.cacheDirectory.deleteRecursively()
    }

    @Test
    fun testPaths() {
        Assert.assertEquals("logs", directoryProvider.logsDirectory.name)
        Assert.assertEquals("temp", directoryProvider.tempDirectory.name)
        Assert.assertEquals("batches", directoryProvider.batchDirectory.name)
        Assert.assertEquals("cache", directoryProvider.cacheDirectory.name)
        Assert.assertEquals("maui.properties", directoryProvider.prefFile.name)
    }

    @Test
    fun createCacheDirectoryTest() {
        val dir = directoryProvider.createCacheDirectory("example").apply {
            deleteOnExit()
        }

        Assert.assertTrue(dir.exists())
    }

    @Test
    fun createTempDirectoryTest() {
        val dir = directoryProvider.createTempDirectory("example").apply {
            deleteOnExit()
        }

        Assert.assertTrue(dir.exists())
    }

    @Test
    fun createTempFileTest() {
        val file = directoryProvider.createTempFile("example", ".tmp").apply {
            deleteOnExit()
        }

        Assert.assertTrue(file.exists())
    }

    @Test
    fun cleanTempDirectoryTest() {
        val dir = directoryProvider.createTempDirectory("example")
        val file1 = dir.resolve("in_temp_dir.txt").apply {
            createNewFile()
        }
        val file2 = directoryProvider.createTempFile("file2", ".txt")

        Assert.assertTrue(dir.exists())
        Assert.assertTrue(file1.exists())
        Assert.assertTrue(file2.exists())
        Assert.assertEquals(2, directoryProvider.tempDirectory.listFiles()?.size)

        directoryProvider.cleanTempDirectory()

        Assert.assertEquals(0, directoryProvider.tempDirectory.listFiles()?.size)
    }

    @Test
    fun deleteCachedFilesTest() {
        val dir1 = directoryProvider.createCacheDirectory("example1")
        val dir2 = directoryProvider.createCacheDirectory("example2")

        val file1 = dir1.resolve("file1.txt").apply {
            createNewFile()
        }
        val file2 = dir2.resolve("file2.txt").apply {
            createNewFile()
        }

        val files = mutableListOf<File>()

        dir1.listFiles()?.let { files.addAll(it) }
        dir2.listFiles()?.let { files.addAll(it) }

        val nonCacheFile = directoryProvider.createTempFile("test", ".txt")

        files.add(nonCacheFile)

        Assert.assertEquals(3, files.size)
        Assert.assertEquals(2, directoryProvider.cacheDirectory.listFiles()?.size)

        directoryProvider.deleteCachedFiles(files)

        Assert.assertEquals(0, directoryProvider.cacheDirectory.listFiles()?.size)
        Assert.assertFalse(file1.exists())
        Assert.assertFalse(file2.exists())
        Assert.assertTrue(nonCacheFile.exists())
    }
}