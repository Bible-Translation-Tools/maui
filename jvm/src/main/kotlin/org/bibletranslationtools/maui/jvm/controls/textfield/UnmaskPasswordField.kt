package org.bibletranslationtools.maui.jvm.controls.textfield

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.layout.StackPane
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.addClass
import tornadofx.attachTo
import tornadofx.filterInput
import tornadofx.objectBinding

class UnmaskPasswordField : StackPane() {
    val iconProperty = SimpleObjectProperty<Ikon>()
    val promptTextProperty = SimpleStringProperty("")
    val textProperty = SimpleStringProperty()
    val onActionProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()

    private val showIcon = FontIcon(MaterialDesign.MDI_EYE)
    private val hideIcon = FontIcon(MaterialDesign.MDI_EYE_OFF)
    private val showPasswordProperty = SimpleBooleanProperty()

    private lateinit var textField: TextField
    private lateinit var passwordField: PasswordField

    init {
        addClass("wa-password-field")

        textField {
            textField = this

            leftProperty().bind(iconProperty.objectBinding {
                it?.let { FontIcon(it) }
            })
            visibleProperty().bind(showPasswordProperty)
            promptTextProperty().bind(promptTextProperty)
            textProperty().bindBidirectional(textProperty)
            onActionProperty().bind(onActionProperty)

            right = hideIcon.apply {
                setOnMouseClicked {
                    toggleShowIcon()
                    passwordField.requestFocus()
                }
            }

            filterInput {
                !it.controlNewText.contains("\\s".toRegex())
            }
        }

        passwordField {
            passwordField = this

            leftProperty().bind(iconProperty.objectBinding {
                it?.let { FontIcon(it) }
            })
            visibleProperty().bind(showPasswordProperty.not())
            promptTextProperty().bind(promptTextProperty)
            textProperty().bindBidirectional(textProperty)
            onActionProperty().bind(onActionProperty)

            right = showIcon.apply {
                setOnMouseClicked {
                    toggleShowIcon()
                    textField.requestFocus()
                }
            }

            filterInput {
                !it.controlNewText.contains("\\s".toRegex())
            }
        }
    }

    private fun toggleShowIcon() {
        showPasswordProperty.set(showPasswordProperty.value.not())
    }
}

fun EventTarget.unmaskPasswordField(op: UnmaskPasswordField.() -> Unit = {}) =
    UnmaskPasswordField().attachTo(this, op)