package org.bibletranslationtools.maui.jvm.mappers

import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.jvm.data.MediaItem
import javax.inject.Inject

class MediaMapper @Inject constructor() : IMapper<Media, MediaItem> {
    override fun fromEntity(type: Media): MediaItem {
        return MediaItem(type)
    }

    override fun toEntity(type: MediaItem): Media {
        return Media(
            type.file,
            type.language,
            type.resourceType,
            type.book,
            if (!type.chapter.isNullOrBlank()) type.chapter!!.toInt() else null,
            type.mediaExtension,
            type.mediaQuality,
            type.grouping,
            type.status,
            type.statusMessage,
            type.parentFile,
            type.selected,
            type.removed
        )
    }
}
