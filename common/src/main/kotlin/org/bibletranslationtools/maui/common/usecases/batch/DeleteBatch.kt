package org.bibletranslationtools.maui.common.usecases.batch

import io.reactivex.Completable
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.persistence.IBatchRepository
import javax.inject.Inject

class DeleteBatch @Inject constructor(private val batchRepository: IBatchRepository) {
    fun delete(batch: Batch): Completable {
        return Completable.fromCallable {
            batchRepository.deleteBatch(batch)
        }
    }
}