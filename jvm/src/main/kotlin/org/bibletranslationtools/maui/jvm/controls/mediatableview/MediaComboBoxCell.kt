package org.bibletranslationtools.maui.jvm.controls.mediatableview

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableListValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.TableCell
import org.bibletranslationtools.maui.jvm.data.MediaItem
import tornadofx.enableWhen
import tornadofx.rowItem

class MediaComboBoxCell<T>(
    options: ObservableListValue<T>,
    editable: Boolean = true
) : TableCell<MediaItem, T>() {

    val titleProperty = SimpleStringProperty()
    val enableProperty = SimpleBooleanProperty(true)

    private val selectedItemProperty = SimpleObjectProperty<T>()
    private val onOptionChangedProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()
    private val onItemReadyProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()

    private val mediaComboBox = MediaComboBox(options, editable, false).apply {
        setOnOptionChanged {
            onOptionChangedProperty.value?.handle(ActionEvent(it, this))
        }

        setOnError {
            selectionModel.select(selectedItemProperty.value)
        }

        enableWhen(enableProperty)
    }

    override fun updateItem(item: T?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty) {
            graphic = null
            return
        }

        onItemReadyProperty.value?.handle(ActionEvent(rowItem, this))

        graphic = mediaComboBox.apply {
            titleProperty.set(this@MediaComboBoxCell.titleProperty.value)

            var selected = item
            if (items.isNotEmpty() && !items.contains(item)) {
                selected = null
            }

            selectedItemProperty.set(selected)
            selectionModel.select(selected)
            value = selected
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun setOnOptionChanged(op: (T) -> Unit) {
        onOptionChangedProperty.set(EventHandler {
            op(it.source as T)
        })
    }

    fun setOnItemReady(op: (MediaItem) -> Unit) {
        onItemReadyProperty.set(EventHandler {
            op(it.source as MediaItem)
        })
    }
}