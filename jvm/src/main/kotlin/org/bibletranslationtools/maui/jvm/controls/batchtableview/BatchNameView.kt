package org.bibletranslationtools.maui.jvm.controls.batchtableview

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*
import tornadofx.FX.Companion.messages

class BatchNameView : HBox() {

    val nameProperty = SimpleStringProperty()
    val editingProperty = SimpleBooleanProperty()

    private val editActionProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()
    private val saveActionProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()

    init {
        addClass("name-cell-view")

        label {
            addClass("file-icon")
            graphic = FontIcon(MaterialDesign.MDI_FILE)
        }
        label {
            addClass("name")

            textProperty().bind(nameProperty)
            visibleProperty().bind(editingProperty.not())
            managedProperty().bind(visibleProperty())
        }
        textfield {
            addClass("name-edit-input")
            textProperty().bindBidirectional(nameProperty)

            onActionProperty().bind(saveActionProperty)

            editingProperty.onChange {
                if (it) {
                    requestFocus()
                    selectAll()
                }
            }

            visibleProperty().bind(editingProperty)
            managedProperty().bind(visibleProperty())
        }
        region {
            hgrow = Priority.ALWAYS
        }
        button {
            addClass("btn", "btn--icon", "btn--edit")
            graphic = FontIcon(MaterialDesign.MDI_PENCIL)

            onActionProperty().bind(editActionProperty)

            visibleProperty().bind(editingProperty.not())
            managedProperty().bind(visibleProperty())
        }
        button(messages["save"]) {
            addClass("btn", "btn--icon", "btn--edit")
            graphic = FontIcon(MaterialDesign.MDI_CHECK)

            onActionProperty().bind(saveActionProperty)

            visibleProperty().bind(editingProperty)
            managedProperty().bind(visibleProperty())
        }
    }

    fun setOnEdit(op: () -> Unit) {
        editActionProperty.set(EventHandler { op.invoke() })
    }

    fun setOnSave(op: () -> Unit) {
        saveActionProperty.set(EventHandler { op.invoke() })
    }
}