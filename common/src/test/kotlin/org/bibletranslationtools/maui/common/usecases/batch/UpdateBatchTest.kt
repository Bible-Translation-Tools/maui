package org.bibletranslationtools.maui.common.usecases.batch

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.persistence.IBatchRepository
import org.junit.Test
import kotlin.io.path.createTempFile

class UpdateBatchTest {

    private val batchRepository = mockk<IBatchRepository> {
        every { saveBatch(any()) } returns Unit
    }

    @Test
    fun updateBatchTest() {
        val batchFile = createTempFile("batch", ".maui").toFile().apply {
            createNewFile()
            deleteOnExit()
        }
        val batch = Batch(
            batchFile,
            "Batch",
            "2024-01-01T01-01-01",
            lazy { listOf() }
        )

        val result = UpdateBatch(batchRepository).update(batch).test()

        result.assertComplete()
        result.assertNoErrors()
    }
}