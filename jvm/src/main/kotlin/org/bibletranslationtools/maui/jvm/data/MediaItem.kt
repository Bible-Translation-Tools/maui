package org.bibletranslationtools.maui.jvm.data

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.bibletranslationtools.maui.common.data.*
import org.bibletranslationtools.maui.common.extensions.CompressedExtensions
import org.bibletranslationtools.maui.jvm.ui.MediaItemComparator
import tornadofx.*
import java.io.File

data class MediaItem(private val data: Media) : Comparable<MediaItem> {

    val file = data.file

    val languageProperty = SimpleStringProperty(data.language)
    var language: String? by languageProperty

    val resourceTypeProperty = SimpleStringProperty(data.resourceType)
    var resourceType: String? by resourceTypeProperty

    val bookProperty = SimpleStringProperty(data.book)
    var book: String? by bookProperty

    val chapterProperty = SimpleStringProperty(data.chapter?.toString())
    var chapter: String? by chapterProperty

    val mediaExtensionProperty = SimpleObjectProperty<MediaExtension>(data.mediaExtension)
    var mediaExtension: MediaExtension? by mediaExtensionProperty

    val mediaQualityProperty = SimpleObjectProperty<MediaQuality>(data.mediaQuality)
    var mediaQuality: MediaQuality? by mediaQualityProperty

    val groupingProperty = SimpleObjectProperty<Grouping>(data.grouping)
    var grouping: Grouping? by groupingProperty

    val statusProperty = SimpleObjectProperty<FileStatus>(data.status)
    var status: FileStatus? by statusProperty

    val statusMessageProperty = SimpleStringProperty(data.statusMessage)
    var statusMessage: String? by statusMessageProperty

    val parentFileProperty = SimpleObjectProperty<File>(data.parentFile)
    var parentFile: File? by parentFileProperty

    val selectedProperty = SimpleBooleanProperty(data.selected)
    var selected by selectedProperty

    val removedProperty = SimpleBooleanProperty(data.removed)
    var removed by removedProperty

    val isContainerProperty = SimpleBooleanProperty(data.isContainer)
    val isContainer by isContainerProperty

    val isCompressedProperty = SimpleBooleanProperty(data.isCompressed)
    val isCompressed by isCompressedProperty

    val isContainerAndCompressedProperty = SimpleBooleanProperty(data.isContainerAndCompressed)
        .or(isContainerProperty.and(mediaExtensionProperty.booleanBinding {
            CompressedExtensions.isSupported(it.toString())
        }))
    val isContainerAndCompressed by isContainerAndCompressedProperty

    val mediaExtensionAvailableProperty = SimpleBooleanProperty(isContainer)
    val mediaExtensionAvailable by mediaExtensionAvailableProperty

    val mediaQualityAvailableProperty = isContainerAndCompressedProperty.or(isCompressedProperty)
    val mediaQualityAvailable by mediaQualityAvailableProperty

    override fun compareTo(other: MediaItem): Int {
        return MediaItemComparator().compare(this, other)
    }
}
