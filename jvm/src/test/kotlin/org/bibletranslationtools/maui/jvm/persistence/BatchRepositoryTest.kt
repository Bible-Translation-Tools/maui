package org.bibletranslationtools.maui.jvm.persistence

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.junit.Assert
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory as createTempDir

class BatchRepositoryTest {

    private val directoryProvider = mockk<IDirectoryProvider> {
        every { batchDirectory } returns createTempDir("temp").toFile().apply {
            deleteOnExit()
        }
        every { deleteCachedFiles(any()) } returns Unit
    }

    @Test
    fun createBatchTest() {
        val batchFile = directoryProvider.batchDirectory.resolve("batch.maui").apply {
            deleteOnExit()
        }
        val repo = BatchRepository(directoryProvider)

        Assert.assertFalse(batchFile.exists())

        val batch = createBatch(batchFile)

        Assert.assertTrue(batchFile.exists())
        Assert.assertTrue(batchFile.length() > 0)
        Assert.assertEquals(1, directoryProvider.batchDirectory.listFiles()?.size)

        val batches = repo.getAll()
        val created = batches.firstOrNull()

        created?.let {
            Assert.assertEquals("batch.maui", it.file.name)
            Assert.assertEquals("Batch", it.name)
            Assert.assertEquals("2024-01-01T01-01-01", it.created)
            Assert.assertEquals(batch.media.value, it.media.value)
        }
    }

    @Test
    fun saveBatchTest() {
        val repo = BatchRepository(directoryProvider)
        val batchFile = directoryProvider.batchDirectory.resolve("batch.maui").apply {
            deleteOnExit()
        }
        val batch = createBatch(batchFile)

        val updatedBatch = batch.copy(name = "Changed Batch")
        repo.saveBatch(updatedBatch)

        val batches = repo.getAll()
        val updated = batches.firstOrNull()

        Assert.assertTrue(batches.isNotEmpty())

        updated?.let {
            Assert.assertEquals(updatedBatch.name, it.name)
            Assert.assertEquals("Changed Batch", it.name)
        }
    }

    @Test
    fun getAllTest() {
        val repo = BatchRepository(directoryProvider)
        val batchFile = directoryProvider.batchDirectory.resolve("batch.maui").apply {
            deleteOnExit()
        }
        createBatch(batchFile)

        val batches = repo.getAll()

        Assert.assertTrue(batches.isNotEmpty())
        Assert.assertEquals(1, batches.size)

        val batchFile2 = directoryProvider.batchDirectory.resolve("batch2.maui").apply {
            deleteOnExit()
        }
        createBatch(batchFile2)

        val batches2 = repo.getAll()

        Assert.assertEquals(2, batches2.size)

        val batchFileNames = batches2.map { it.file.name }
        Assert.assertTrue(batchFileNames.contains("batch.maui"))
        Assert.assertTrue(batchFileNames.contains("batch2.maui"))
    }

    @Test
    fun deleteBatchTest() {
        val repo = BatchRepository(directoryProvider)
        val batchFile = directoryProvider.batchDirectory.resolve("batch.maui")
        val batch = createBatch(batchFile)

        Assert.assertTrue(batchFile.exists())

        repo.deleteBatch(batch)

        Assert.assertFalse(batchFile.exists())
    }

    private fun createBatch(file: File): Batch {
        val batch = Batch(
            file,
            "Batch",
            "2024-01-01T01-01-01",
            lazy {
                listOf(
                    Media(File("/example1.wav")),
                    Media(File("/example2.wav"))
                )
            }
        )

        BatchRepository(directoryProvider).createBatch(batch)

        return batch
    }
}