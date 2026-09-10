package org.bibletranslationtools.maui.jvm.controls.mediatableview

import com.jfoenix.controls.JFXComboBox
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableListValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import org.bibletranslationtools.maui.jvm.ui.events.ErrorOccurredEvent
import tornadofx.*
import tornadofx.FX.Companion.messages
import java.text.MessageFormat

class MediaComboBox<T>(
    options: ObservableListValue<T>,
    editable: Boolean = true,
    prompt: Boolean = true
) : JFXComboBox<T>(options) {
    val titleProperty = SimpleStringProperty()

    private val onOptionChangedProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()
    private val onErrorProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()

    init {
        addClass("media-combo-box")
        isEditable = editable

        if (prompt) {
            promptTextProperty().bind(titleProperty)
            tooltip { textProperty().bind(titleProperty) }
        }

        focusedProperty().onChange {
            if (!it) onAction.handle(ActionEvent())
        }

        setOnAction {
            when {
                selectedItem in items -> {
                    onOptionChangedProperty.value?.handle(ActionEvent(selectedItem, this))
                }
                selectedItem != null && items.isNotEmpty() -> {
                    if (selectedItem.toString().isNotEmpty()) {
                        val message = MessageFormat.format(
                            messages["optionNotFound"],
                            selectedItem,
                            titleProperty.value
                        )
                        FX.eventbus.fire(ErrorOccurredEvent(message))
                    }
                    onErrorProperty.value?.handle(ActionEvent(selectedItem, this))
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun setOnOptionChanged(op: (T) -> Unit) {
        onOptionChangedProperty.set(EventHandler {
            op(it.source as T)
        })
    }

    @Suppress("UNCHECKED_CAST")
    fun setOnError(op: (T) -> Unit) {
        onErrorProperty.set(EventHandler {
            op(it.source as T)
        })
    }
}