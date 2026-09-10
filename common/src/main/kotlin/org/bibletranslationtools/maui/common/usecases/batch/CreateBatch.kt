package org.bibletranslationtools.maui.common.usecases.batch

import io.reactivex.Single
import org.bibletranslationtools.maui.common.MauiInfo
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.persistence.IBatchRepository
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class CreateBatch @Inject constructor(
    private val batchRepository: IBatchRepository,
    private val directoryProvider: IDirectoryProvider
) {
    fun create(media: List<Media>): Single<Batch> {
        return Single.fromCallable {
            if (media.isEmpty()) throw IllegalArgumentException("No supported files to import.")

            val filename = "${UUID.randomUUID()}.${MauiInfo.EXTENSION}"
            val created = LocalDateTime.now().toString()
            val name = generateInitialName(media)

            val batch = Batch(
                directoryProvider.batchDirectory.resolve(filename),
                name,
                created,
                lazy { media }
            )

            batchRepository.createBatch(batch)

            batch
        }
    }

    private fun generateInitialName(mediaItems: List<Media>): String {
        var language: String? = null
        var resourceType: String? = null
        var book: String? = null
        var chapter: Int? = null
        var extension = ""

        var name = ""

        mediaItems.forEach { media ->
            if (language == null) language = media.language
            if (resourceType == null) resourceType = media.resourceType
            if (book == null) book = media.book
            if (chapter == null) chapter = media.chapter

            extension = media.file.extension
        }

        if (language != null) name += "${language}_"
        if (resourceType != null) name += "${resourceType}_"
        if (book != null) name += "${book}_"
        if (chapter != null) name += "${chapter}_"

        return (name.ifBlank { "untitled_batch_" }) + extension
    }
}