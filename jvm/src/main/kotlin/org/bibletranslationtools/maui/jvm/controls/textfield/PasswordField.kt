package org.bibletranslationtools.maui.jvm.controls.textfield

import javafx.event.EventTarget
import org.controlsfx.control.textfield.CustomPasswordField
import tornadofx.addClass
import tornadofx.attachTo

class PasswordField : CustomPasswordField() {
    init {
        addClass("wa-text-field")
    }
}

fun EventTarget.passwordField(op: PasswordField.() -> Unit = {}) =
    PasswordField().attachTo(this, op)
