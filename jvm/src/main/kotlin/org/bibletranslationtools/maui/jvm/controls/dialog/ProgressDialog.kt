package org.bibletranslationtools.maui.jvm.controls.dialog

import javafx.animation.Interpolator
import javafx.animation.RotateTransition
import javafx.animation.Timeline
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Priority
import javafx.scene.transform.Rotate
import javafx.util.Duration
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*

class ProgressDialog : MauiDialog() {
    
    val showProgressBarProperty = SimpleBooleanProperty()
    val progressProperty = SimpleDoubleProperty()

    private lateinit var rotateAnimation: RotateTransition

    private val content = vbox {
        addClass("progress-dialog")

        hbox {
            addClass("header")

            label {
                graphic = FontIcon(MaterialDesign.MDI_AUTORENEW).apply {
                    rotate(Duration.millis(1000.0), 360.0, play = false) {
                        rotateAnimation = this
                        axis = Rotate.Z_AXIS
                        cycleCount = Timeline.INDEFINITE
                        interpolator = Interpolator.LINEAR
                    }
                }
            }

            label(titleTextProperty)
        }
        vbox {
            addClass("body")
            vgrow = Priority.ALWAYS
            label {
                addClass("message")
                textProperty().bind(messageTextProperty)
            }
        }
        vbox {
            addClass("progress")

            progressbar {
                addClass("wa-progress-bar")

                fitToParentWidth()
                progressProperty().bind(progressProperty)
                visibleProperty().bind(showProgressBarProperty)
                managedProperty().bind(visibleProperty())
            }
        }
    }

    init {
        importStylesheet(AppResources.load("/css/progress-dialog.css"))

        setContent(content)
    }

    override fun onDock() {
        runLater {
            rotateAnimation.play()
        }
    }

    override fun onUndock() {
        runLater {
            rotateAnimation.pause()
        }
    }
}

fun progressDialog(op: ProgressDialog.() -> Unit) = ProgressDialog().apply(op)

class ProgressDialogEvent(
    val show: Boolean,
    val title: String? = null,
    val message: String? = null,
    val showProgressBar: Boolean = false,
    val progressProperty: DoubleProperty = SimpleDoubleProperty()
) : FXEvent()