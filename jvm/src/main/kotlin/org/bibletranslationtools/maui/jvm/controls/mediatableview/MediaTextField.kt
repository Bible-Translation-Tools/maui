package org.bibletranslationtools.maui.jvm.controls.mediatableview

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import tornadofx.*

private const val MAX_CHAPTER_LENGTH = 3

class MediaTextField : TextField() {

    val titleProperty = SimpleStringProperty()
    private val onTextChangedProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()

    init {
        addClass("media-text-field")

        promptTextProperty().bind(titleProperty)
        tooltipProperty().bind(titleProperty.objectBinding {
            it?.let { Tooltip(it).apply {
                Tooltip.install(this@MediaTextField, this)
            }}
        })

        filterInput {
            it.controlNewText.isInt() && it.controlNewText.length <= MAX_CHAPTER_LENGTH
        }

        textProperty().onChange {
            if (text.isNotEmpty()) {
                onTextChangedProperty.value?.handle(ActionEvent(it, this))
            }
        }

        setOnAction {
            onTextChangedProperty.value?.handle(ActionEvent(text, this))
        }
    }

    fun setOnTextChanged(op: (String) -> Unit) {
        onTextChangedProperty.set(EventHandler {
            op(it.source as String)
        })
    }
}