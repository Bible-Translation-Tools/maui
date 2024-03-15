package org.bibletranslationtools.maui.jvm.controls.mediatableview

import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.util.StringConverter
import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.MediaExtension
import org.bibletranslationtools.maui.common.data.MediaQuality
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.bindColumnSortComparator
import org.bibletranslationtools.maui.jvm.bindSortPolicy
import org.bibletranslationtools.maui.jvm.bindTableSortComparator
import org.bibletranslationtools.maui.jvm.customizeScrollbarSkin
import org.bibletranslationtools.maui.jvm.data.FileStatusFilter
import org.bibletranslationtools.maui.jvm.data.MediaItem
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*
import tornadofx.FX.Companion.messages

class MediaTableView(
    val media: ObservableList<MediaItem>
) : TableView<MediaItem>(media) {

    val languagesProperty = SimpleListProperty<String>()
    val resourceTypesProperty = SimpleListProperty<String>()
    val booksProperty = SimpleListProperty<String>()
    val mediaExtensionsProperty = SimpleListProperty<MediaExtension>()
    val mediaQualitiesProperty = SimpleListProperty<MediaQuality>()
    val groupingsProperty = SimpleListProperty<Grouping>()
    val statusFilterProperty = SimpleListProperty<FileStatusFilter>()

    init {
        addClass("media-table-view")
        importStylesheet(AppResources.load("/css/media-table-view.css"))

        runLater { customizeScrollbarSkin() }

        vgrow = Priority.ALWAYS
        columnResizePolicy = CONSTRAINED_RESIZE_POLICY

        resetStatusFilter()

        placeholder = borderpane {
            center = vbox {
                addClass("placeholder")

                label {
                    addClass("placeholder-icon")
                    graphic = FontIcon(MaterialDesign.MDI_FILE_OUTLINE)
                }
                label(messages["noMediaPrompt"]) {
                    addClass("placeholder-text")
                }
            }
        }

        bindSortPolicy()
        bindTableSortComparator()

        column("", Node::class) {
            addClass("checkbox-column")

            setCellValueFactory {
                checkbox {
                    addClass("wa-checkbox")
                    it.value.selectedProperty.onChange {
                        isSelected = it
                    }
                    action {
                        it.value.selected = isSelected
                    }
                }.toProperty()
            }

            graphic = checkbox {
                addClass("wa-checkbox")
                action {
                    items.forEach { it.selected = isSelected }
                }
            }

            minWidth = 40.0
            bindColumnWidth(3.0)

            isSortable = false
            isReorderable = true
        }

        column(messages["fileName"], String::class) {
            addClass("file-column")

            setCellValueFactory {
                it.value.file.name.toProperty()
            }

            setComparator { o1, o2 ->
                o1.compareTo(o2, ignoreCase = true)
            }
            bindColumnSortComparator()
            bindColumnWidth(22.2)
            isReorderable = false
        }

        column("", String::class) {
            graphic = MediaComboBox(languagesProperty).apply {
                hgrow = Priority.ALWAYS

                titleProperty.set(messages["language"])
                setOnOptionChanged { newValue ->
                    runLater { value = null }
                    this@MediaTableView.items.forEach {
                        it.language = newValue
                    }
                }
            }

            setCellValueFactory {
                it.value.languageProperty
            }

            setCellFactory {
                MediaComboBoxCell(languagesProperty).apply {
                    titleProperty.set(messages["language"])
                    setOnOptionChanged {
                        rowItem.language = it
                    }
                }
            }

            bindColumnWidth(9.0)
            isSortable = false
            isReorderable = false
        }

        column("", String::class) {
            graphic = MediaComboBox(resourceTypesProperty).apply {
                titleProperty.set(messages["resourceType"])
                setOnOptionChanged { newValue ->
                    runLater { value = null }
                    this@MediaTableView.items.forEach {
                        it.resourceType = newValue
                    }
                }
            }

            setCellValueFactory {
                it.value.resourceTypeProperty
            }

            setCellFactory {
                MediaComboBoxCell(resourceTypesProperty).apply {
                    titleProperty.set(messages["resourceType"])
                    setOnOptionChanged {
                        rowItem.resourceType = it
                    }
                }
            }

            bindColumnWidth(11.0)
            isSortable = false
            isReorderable = false
        }

        column("", String::class) {
            graphic = MediaComboBox(booksProperty).apply {
                titleProperty.set(messages["book"])
                setOnOptionChanged { newValue ->
                    runLater { value = null }
                    this@MediaTableView.items.forEach {
                        it.book = newValue
                    }
                }
            }

            setCellValueFactory {
                it.value.bookProperty
            }

            setCellFactory {
                MediaComboBoxCell(booksProperty).apply {
                    titleProperty.set(messages["book"])
                    setOnOptionChanged {
                        rowItem.book = it
                    }
                }
            }

            minWidth = 70.0
            bindColumnWidth(6.0)
            isSortable = false
            isReorderable = false
        }

        column("", String::class) {
            graphic = MediaTextField().apply {
                titleProperty.set(messages["chapter"])
                setOnAction {
                    items.forEach { it.chapter = text }
                    text = null
                }
            }

            setCellValueFactory {
                it.value.chapterProperty
            }

            setCellFactory {
                TextCell().apply {
                    setOnTextChanged {
                        rowItem.chapter = it
                    }
                }
            }

            bindColumnWidth(7.0)
            isSortable = false
            isReorderable = false
        }

        column("", MediaExtension::class) {
            graphic = MediaComboBox(mediaExtensionsProperty, false).apply {
                titleProperty.set(messages["mediaExtension"])
                setOnOptionChanged { newValue ->
                    runLater { value = null }
                    this@MediaTableView.items.forEach {
                        it.mediaExtension = newValue
                    }
                }
            }

            setCellValueFactory {
                it.value.mediaExtensionProperty
            }

            setCellFactory {
                MediaComboBoxCell(mediaExtensionsProperty, false).apply {
                    setOnOptionChanged {
                        rowItem.mediaExtension = it
                    }
                }
            }

            bindColumnWidth(12.0)
            isSortable = false
            isReorderable = false
        }

        column("", MediaQuality::class) {
            graphic = MediaComboBox(mediaQualitiesProperty, false).apply {
                titleProperty.set(messages["mediaQuality"])
                setOnOptionChanged { newValue ->
                    runLater { value = null }
                    this@MediaTableView.items.forEach {
                        it.mediaQuality = newValue
                    }
                }
            }

            setCellValueFactory {
                it.value.mediaQualityProperty
            }

            setCellFactory {
                MediaComboBoxCell(mediaQualitiesProperty, false).apply {
                    setOnOptionChanged {
                        rowItem.mediaQuality = it
                    }
                }
            }

            bindColumnWidth(10.5)
            isSortable = false
            isReorderable = false
        }

        column("", Grouping::class) {
            graphic = MediaComboBox(groupingsProperty, false).apply {
                titleProperty.set(messages["grouping"])
                setOnOptionChanged { newValue ->
                    runLater { value = null }
                    this@MediaTableView.items.forEach {
                        it.grouping = newValue
                    }
                }
            }

            setCellValueFactory {
                it.value.groupingProperty
            }

            setCellFactory {
                MediaComboBoxCell(groupingsProperty, false).apply {
                    setOnOptionChanged {
                        rowItem.grouping = it
                    }
                }
            }

            bindColumnWidth(9.0)
            isSortable = false
            isReorderable = false
        }

        column("", Node::class) {
            graphic = MediaComboBox(statusFilterProperty, false).apply {
                addClass("media-combo-box--filter")
                titleProperty.set(messages["status"])
                converter = object : StringConverter<FileStatusFilter>() {
                    override fun toString(filter: FileStatusFilter?): String? {
                        return filter?.let { messages[filter.toString()] }
                    }
                    override fun fromString(string: String?): FileStatusFilter? {
                        return string?.let { FileStatusFilter.of(string) }
                    }

                }
                setOnOptionChanged { filter ->
                    when (filter) {
                        FileStatusFilter.PROCESSED -> filterProcessed()
                        FileStatusFilter.REJECTED -> filterRejected()
                        FileStatusFilter.GROUP -> sortByStatus()
                        FileStatusFilter.RESET -> resetStatusFilter()
                        else -> {}
                    }
                    runLater { value = null }
                }
            }

            setCellValueFactory { item ->
                statusColumnView(item.value).toProperty()
            }

            bindColumnWidth(10.0)
            isSortable = false
            isReorderable = false
        }
    }
}

internal fun <S, T> TableColumn<S, T>.bindColumnWidth(percent: Double) {
    isResizable = false
    prefWidthProperty().bind(tableView.widthProperty().multiply(percent / 100.0))
}

internal fun TableView<MediaItem>.filterProcessed() {
    (items as SortedFilteredList).predicate = { mediaItem ->
        mediaItem.status == FileStatus.PROCESSED
    }
}

internal fun TableView<MediaItem>.filterRejected() {
    (items as SortedFilteredList).predicate = { mediaItem ->
        mediaItem.status == FileStatus.REJECTED
    }
}

internal fun TableView<MediaItem>.sortByStatus() {
    resetStatusFilter()
    (items as SortedFilteredList).sortedItems.comparator = compareBy { it.status }
}

internal fun TableView<MediaItem>.resetStatusFilter() {
    (items as SortedFilteredList<MediaItem>).sortedItems.comparator =
        compareBy(String.CASE_INSENSITIVE_ORDER) { it.file.name }
    (items as SortedFilteredList<MediaItem>).predicate = { true }
}

fun EventTarget.mediaTableView(
    values: ObservableList<MediaItem>,
    op: MediaTableView.() -> Unit = {}
) = MediaTableView(values).attachTo(this, op)