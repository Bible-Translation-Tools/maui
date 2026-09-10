package org.bibletranslationtools.maui.jvm.ui.components

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.ui.UploadTarget
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*
import tornadofx.FX.Companion.messages

class UploadTargetHeader : HBox() {
    val uploadTargetProperty = SimpleObjectProperty<UploadTarget>()
    val uploadTargets = observableListOf<UploadTarget>()
    val uploadTargetTextProperty = SimpleStringProperty()

    init {
        addClass("upload-target-header")

        importStylesheet(AppResources.load("/css/upload-target-header.css"))

        hbox {
            addClass("upload-target-header__title")

            label {
                addClass("upload-target-header__title-dot")
                graphic = FontIcon(MaterialDesign.MDI_CHECKBOX_BLANK_CIRCLE)
            }
            label {
                addClass("upload-target-header__title-text")
                textProperty().bind(uploadTargetTextProperty)
            }
        }

        region {
            hgrow = Priority.ALWAYS
        }

        hbox {
            addClass("upload-target-header__select")
            label(messages["changeUploadTarget"]) {
                addClass("upload-target-header__select-text")
            }
            combobox(uploadTargetProperty, uploadTargets) {
                addClass("wa-combobox", "upload-target-header__select-combo")
            }
        }
    }
}

fun EventTarget.uploadTargetHeader(op: UploadTargetHeader.() -> Unit = {}): UploadTargetHeader {
    val uploadTargetHeader = UploadTargetHeader()
    return opcr(this, uploadTargetHeader, op)
}