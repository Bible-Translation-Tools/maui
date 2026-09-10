package org.bibletranslationtools.maui.jvm.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.bibletranslationtools.maui.common.MauiInfo
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.persistence.IBatchRepository
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.slf4j.LoggerFactory
import javax.inject.Inject

class BatchRepository @Inject constructor(private val directoryProvider: IDirectoryProvider) : IBatchRepository {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val batchMapper = ObjectMapper().registerKotlinModule()

    override fun createBatch(batch: Batch) {
        if (!batch.file.exists()) {
            batch.file.createNewFile()
        }
        saveBatch(batch)
    }

    override fun saveBatch(batch: Batch) {
        try {
            val jsonStr = batchMapper.writeValueAsString(batch)
            batch.file.writeText(jsonStr)
        } catch (e: Exception) {
            logger.error("Error in saveBatch for ${batch.file}", e)
        }
    }

    override fun deleteBatch(batch: Batch) {
        directoryProvider.deleteCachedFiles(
            batch.media.value.map { it.file }
        )
        batch.file.delete()
    }

    override fun getAll(): List<Batch> {
        return directoryProvider.batchDirectory.listFiles()
            ?.filter { it.isFile && it.extension == MauiInfo.EXTENSION }
            ?.mapNotNull { file ->
                try {
                    val json = file.readText()
                    batchMapper.readValue(json)
                } catch (e: Exception) {
                    logger.error("Error in getAll for file $file", e)
                    null
                }
            } ?: listOf()
    }
}
