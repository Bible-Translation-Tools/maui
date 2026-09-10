package org.bibletranslationtools.maui.common.persistence

import org.bibletranslationtools.maui.common.data.Batch

interface IBatchRepository {
    fun createBatch(batch: Batch)
    fun saveBatch(batch: Batch)
    fun deleteBatch(batch: Batch)
    fun getAll(): List<Batch>
}