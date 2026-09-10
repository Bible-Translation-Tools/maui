package org.bibletranslationtools.maui.jvm.controls.dialog

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.layout.Priority
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.controls.textfield.textField
import org.bibletranslationtools.maui.jvm.controls.textfield.unmaskPasswordField
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*

class LoginDialog : MauiDialog() {
    val serverProperty = SimpleStringProperty()
    val userProperty = SimpleStringProperty()
    val passwordProperty = SimpleStringProperty()

    private val onActionProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()

    private val content = vbox {
        addClass("login-dialog")

        vbox {
            addClass("header")
            label(titleTextProperty) {
                addClass("title")
            }
            label(messageTextProperty) {
                addClass("subtitle")
            }
        }

        textField {
            promptText = messages["serverName"]
            textProperty().bindBidirectional(serverProperty)
            onActionProperty().bind(onActionProperty)
            leftProperty().set(FontIcon(MaterialDesign.MDI_WIFI))

            textProperty().onChange {
                runLater {
                    text = sanitizeServerName(it)
                    positionCaret(text.length)
                }
            }
        }

        textField {
            promptText = messages["userName"]
            textProperty().bindBidirectional(userProperty)
            onActionProperty().bind(onActionProperty)
            leftProperty().set(FontIcon(MaterialDesign.MDI_ACCOUNT))

            filterInput {
                !it.controlNewText.contains("\\s".toRegex())
            }
        }

        unmaskPasswordField {
            promptTextProperty.set(messages["password"])
            iconProperty.set(MaterialDesign.MDI_LOCK)
            textProperty.bindBidirectional(passwordProperty)

            onActionProperty.bind(this@LoginDialog.onActionProperty)
        }

        hbox {
            addClass("actions")

            button(messages["login"]) {
                addClass("btn", "btn--primary")
                hgrow = Priority.ALWAYS
                onActionProperty().bind(onActionProperty)
            }

            button(messages["cancel"]) {
                addClass("btn", "btn--secondary")
                hgrow = Priority.ALWAYS
                action { close() }
            }
        }

    }

    init {
        importStylesheet(AppResources.load("/css/login-dialog.css"))

        setContent(content)
    }

    fun setOnAction(op: () -> Unit) {
        onActionProperty.set(EventHandler {
            op.invoke()
        })
    }

    private fun sanitizeServerName(server: String?): String? {
        val scheme = "^.*?://".toRegex()
        val port = ":\\d+.*".toRegex()
        val path = "(\\..*?|\\d+)(/.*?)\$".toRegex()
        val special = "[~!@#$%^&*()?+=\\s:]+".toRegex()

        return server
            ?.replace(scheme, "")
            ?.replace(port, "")
            ?.replace(path, "$1")
            ?.replace(special, "")
    }
}

fun loginDialog(setup: LoginDialog.() -> Unit = {}): LoginDialog {
    val loginDialog = LoginDialog()
    loginDialog.setup()
    return loginDialog
}

class LoginDialogEvent(
    val action: () -> Unit = {},
) : FXEvent()