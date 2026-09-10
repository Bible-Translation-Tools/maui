package org.bibletranslationtools.maui.common.usecases.batch

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.persistence.IBatchRepository
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory as createTempDir

class CreateBatchTest {

    private val batchRepository = mockk<IBatchRepository> {
        every { createBatch(any()) } returns Unit
    }
    private val directoryProvider = mockk<IDirectoryProvider> {
        every { batchDirectory } returns createTempDir("batches").toFile().apply {
            deleteOnExit()
        }
    }

    @Test
    fun createBatchTest() {
        val mediaList = listOf(
            Media(File("test1.wav")),
            Media(File("test2.mp3"))
        )

        val result = CreateBatch(
            batchRepository,
            directoryProvider
        ).create(mediaList).test()

        result.assertComplete()
        result.assertNoErrors()
        result.valueCount()
    }

    @Test
    fun createBatchFailedOnEmptyMedia() {
        val mediaList = listOf<Media>()

        val result = CreateBatch(
            batchRepository,
            directoryProvider
        ).create(mediaList).test()

        result.assertNotComplete()
        result.assertError(IllegalArgumentException::class.java)
        result.assertErrorMessage("No supported files to import.")
    }
}