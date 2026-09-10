package org.bibletranslationtools.maui.jvm.controls.dialog

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.customizeScrollbarSkin
import org.bibletranslationtools.maui.jvm.onChangeAndDoNow
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*
import tornadofx.FX.Companion.messages

enum class AlertType {
    INFO,
    CONFIRM
}

class AlertDialog : MauiDialog() {
    val primaryButtonTextProperty = SimpleStringProperty()
    val primaryButtonIconProperty = SimpleObjectProperty<Node>()
    val secondaryButtonTextProperty = SimpleStringProperty()
    val secondaryButtonIconProperty = SimpleObjectProperty<Node>()

    val onPrimaryActionProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()
    val onSecondaryActionProperty = SimpleObjectProperty<EventHandler<ActionEvent>>()

    val isWarningProperty = SimpleBooleanProperty()
    private val normalIcon = FontIcon(MaterialDesign.MDI_CHECK_CIRCLE)
    private val warningIcon = FontIcon(MaterialDesign.MDI_ALERT)

    private lateinit var scroll: Parent

    private val content = vbox {
        addClass("alert-dialog")

        isWarningProperty.onChangeAndDoNow {
            togglePseudoClass("warning", it == true)
        }

        hbox {
            addClass("header")

            label {
                graphicProperty().bind(isWarningProperty.objectBinding {
                    if (it == true) warningIcon else normalIcon
                })
            }

            label(titleTextProperty)
        }

        vbox {
            addClass("body")
            vgrow = Priority.ALWAYS

            label(messageTextProperty) {
                addClass("message")
            }

            scrollpane {
                addClass("details")
                scroll = this

                isFitToWidth = true
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                vgrow = Priority.ALWAYS

                text(detailsTextProperty).apply {
                    wrappingWidthProperty().bind(this@scrollpane.widthProperty().minus(20.0))
                }

                visibleProperty().bind(detailsTextProperty.isNotEmpty)
                managedProperty().bind(visibleProperty())
            }
        }

        hbox {
            addClass("footer")

            button(secondaryButtonTextProperty) {
                addClass("btn", "btn--secondary")

                hgrow = Priority.ALWAYS
                tooltip { textProperty().bind(this@button.textProperty()) }
                graphicProperty().bind(secondaryButtonIconProperty)

                onActionProperty().bind(onSecondaryActionProperty)
                visibleProperty().bind(onSecondaryActionProperty.isNotNull)
                managedProperty().bind(visibleProperty())
            }

            button(primaryButtonTextProperty) {
                addClass("btn", "btn--primary")

                hgrow = Priority.ALWAYS
                tooltip { textProperty().bind(this@button.textProperty()) }
                graphicProperty().bind(primaryButtonIconProperty)

                onActionProperty().bind(onPrimaryActionProperty)
                visibleProperty().bind(onPrimaryActionProperty.isNotNull)
                managedProperty().bind(visibleProperty())
            }

            visibleProperty().bind(
                onSecondaryActionProperty.isNotNull.or(onPrimaryActionProperty.isNotNull)
            )
            managedProperty().bind(visibleProperty())
        }
    }

    init {
        importStylesheet(AppResources.load("/css/alert-dialog.css"))

        setContent(content)
    }

    override fun onDock() {
        scroll.apply {
            runLater { customizeScrollbarSkin() }
        }
    }

    fun setOnSecondaryAction(op: () -> Unit) {
        onSecondaryActionProperty.set(EventHandler { op.invoke() })
    }

    fun setOnPrimaryAction(op: () -> Unit) {
        onPrimaryActionProperty.set(EventHandler { op.invoke() })
    }
}

fun alertDialog(setup: AlertDialog.() -> Unit = {}): AlertDialog {
    val alertDialog = AlertDialog()
    alertDialog.setup()
    return alertDialog
}

class AlertDialogEvent(
    val type: AlertType,
    val title: String,
    val message: String,
    val details: String? = null,
    val primaryText: String = messages["ok"],
    val primaryIcon: FontIcon = FontIcon(MaterialDesign.MDI_CHECK),
    val primaryAction: () -> Unit = {},
    val secondaryText: String = messages["cancel"],
    val secondaryIcon: FontIcon = FontIcon(MaterialDesign.MDI_CLOSE_CIRCLE),
    val secondaryAction: () -> Unit = {},
    val isWarning: Boolean = false,
) : FXEvent()
