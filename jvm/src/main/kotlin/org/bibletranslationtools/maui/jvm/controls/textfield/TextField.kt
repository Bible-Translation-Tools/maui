package org.bibletranslationtools.maui.jvm.controls.textfield

import javafx.event.EventTarget
import org.controlsfx.control.textfield.CustomTextField
import tornadofx.addClass
import tornadofx.attachTo

class TextField : CustomTextField() {
    init {
        addClass("wa-text-field")
    }
}

fun EventTarget.textField(op: TextField.() -> Unit = {}) =
    TextField().attachTo(this, op)