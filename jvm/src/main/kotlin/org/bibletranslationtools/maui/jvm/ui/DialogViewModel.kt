package org.bibletranslationtools.maui.jvm.ui

import javafx.beans.property.DoubleProperty
import org.bibletranslationtools.maui.jvm.controls.dialog.AlertDialogEvent
import org.bibletranslationtools.maui.jvm.controls.dialog.AlertType
import org.bibletranslationtools.maui.jvm.controls.dialog.LoginDialogEvent
import org.bibletranslationtools.maui.jvm.controls.dialog.ProgressDialogEvent
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.ViewModel
import tornadofx.get

class DialogViewModel : ViewModel() {

    fun showProgress(title: String, message: String) {
        val progress = ProgressDialogEvent(
            true,
            title,
            message
        )
        fire(progress)
    }

    fun showProgressWithBar(title: String, message: String, progressProperty: DoubleProperty) {
        val progress = ProgressDialogEvent(
            true,
            title,
            message,
            showProgressBar = true,
            progressProperty = progressProperty
        )
        fire(progress)
    }

    fun hideProgress() {
        fire(ProgressDialogEvent(false))
    }

    fun showLogin(action: () -> Unit) {
        val loginEvent = LoginDialogEvent(action)
        fire(loginEvent)
    }

    fun showError(title: String, message: String, details: String? = null) {
        val error = AlertDialogEvent(
            type = AlertType.INFO,
            title = title,
            message = message,
            details = details,
            isWarning = true
        )
        fire(error)
    }

    fun showSuccess(title: String, message: String, details: String? = null) {
        val success = AlertDialogEvent(
            AlertType.INFO,
            title,
            message,
            details
        )
        fire(success)
    }

    fun showConfirm(
        title: String,
        message: String,
        details: String? = null,
        primaryText: String = messages["ok"],
        primaryIcon: FontIcon = FontIcon(MaterialDesign.MDI_CHECK),
        primaryAction: () -> Unit = {},
        secondaryText: String = messages["cancel"],
        secondaryIcon: FontIcon = FontIcon(MaterialDesign.MDI_CLOSE_CIRCLE),
        secondaryAction: () -> Unit = {},
        isWarning: Boolean = false
    ) {
        val event = AlertDialogEvent(
            AlertType.CONFIRM,
            title,
            message,
            details,
            primaryText,
            primaryIcon,
            primaryAction,
            secondaryText,
            secondaryIcon,
            secondaryAction,
            isWarning
        )
        fire(event)
    }
}