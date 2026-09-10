package org.bibletranslationtools.maui.common.data

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bibletranslationtools.maui.common.extensions.CompressedExtensions
import org.bibletranslationtools.maui.common.extensions.ContainerExtensions
import org.bibletranslationtools.maui.common.extensions.MediaExtensions
import java.io.File

data class Media(
    val file: File,
    val language: String? = null,
    val resourceType: String? = null,
    val book: String? = null,
    val chapter: Int? = null,
    val mediaExtension: MediaExtension? = null,
    val mediaQuality: MediaQuality? = null,
    val grouping: Grouping? = null,
    val status: FileStatus? = null,
    val statusMessage: String? = null,
    val parentFile: File? = null,
    val selected: Boolean = false,
    val removed: Boolean = false
) {
    @JsonIgnore
    val extension = MediaExtensions.of(file.extension)
    @JsonIgnore
    val isContainer = ContainerExtensions.isSupported(extension.norm)
    @JsonIgnore
    val isCompressed =
        !isContainer && CompressedExtensions.isSupported(extension.norm)
    @JsonIgnore
    val isContainerAndCompressed =
        isContainer && CompressedExtensions.isSupported(mediaExtension.toString())
}
