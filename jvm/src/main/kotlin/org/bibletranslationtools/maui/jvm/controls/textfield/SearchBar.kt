package org.bibletranslationtools.maui.jvm.controls.textfield

import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import org.controlsfx.control.textfield.CustomTextField
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.addClass
import tornadofx.attachTo

class SearchBar : CustomTextField() {

    private val searchIcon = FontIcon(MaterialDesign.MDI_MAGNIFY)
    private val clearBtn = Button().apply {
        addClass("btn", "btn--icon")
        graphic = FontIcon(MaterialDesign.MDI_CLOSE)
    }

    init {
        addClass("search-bar", "wa-text-field")

        clearBtn.setOnAction {
            text = ""
            this.requestFocus()
        }
        rightProperty().bind(createGraphicBinding())
    }

    private fun createGraphicBinding(): ObjectBinding<Node> {
        return Bindings.createObjectBinding(
            {
                if (textProperty().isEmpty.value) {
                    searchIcon
                } else {
                    clearBtn
                }
            },
            textProperty()
        )
    }
}

fun EventTarget.searchBar(op: SearchBar.() -> Unit = {}) = SearchBar().attachTo(this, op)