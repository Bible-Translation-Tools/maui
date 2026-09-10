package org.bibletranslationtools.maui.common.data

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bibletranslationtools.maui.common.serializing.MediaDeserializer
import org.bibletranslationtools.maui.common.serializing.MediaSerializer
import java.io.File
import java.util.Objects

data class Batch(
    val file: File,
    val name: String,
    val created: String,
    @JsonSerialize(using = MediaSerializer::class)
    @JsonDeserialize(using = MediaDeserializer::class)
    val media: Lazy<List<Media>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this.javaClass != other?.javaClass) return false

        other as Batch

        if (this.file != other.file) return false
        if (this.created != other.created) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(
            file.absolutePath,
            name,
            created
        )
    }
}