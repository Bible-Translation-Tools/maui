package org.bibletranslationtools.maui.jvm.ui

import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.controls.dialog.*
import org.bibletranslationtools.maui.jvm.onChangeAndDoNow
import org.bibletranslationtools.maui.jvm.ui.startup.StartupPage
import tornadofx.*

class AppWorkspace : Workspace() {

    private val navigator: NavigationMediator by inject()
    private val batchDataStore: BatchDataStore by inject()

    private lateinit var alertDialog: AlertDialog
    private lateinit var progressDialog: ProgressDialog
    private lateinit var loginDialog: LoginDialog

    init {
        header.removeFromParent()

        importStylesheet(AppResources.load("/css/root.css"))
        importStylesheet(AppResources.load("/css/target/dev-target.css"))
        importStylesheet(AppResources.load("/css/target/prod-target.css"))

        importStylesheet(AppResources.load("/css/control.css"))
        importStylesheet(AppResources.load("/css/buttons.css"))

        bindUploadTargetClassToRoot()

        initializeAlertDialog()
        initializeProgressDialog()
        initializeLoginDialog()

        subscribe<AlertDialogEvent> {
            openAlertDialog(it)
        }

        subscribe<ProgressDialogEvent> {
            openProgressDialog(it)
        }

        subscribe<LoginDialogEvent> {
            openLoginDialog(it)
        }
    }

    override fun onBeforeShow() {
        navigator.dock<StartupPage>()
    }

    private fun bindUploadTargetClassToRoot() {
        batchDataStore.uploadTargetProperty.onChangeAndDoNow {
            when (it) {
                UploadTarget.DEV -> {
                    root.addClass(UploadTarget.DEV.styleClass)
                    root.removeClass(UploadTarget.PROD.styleClass)
                }
                else -> {
                    root.addClass(UploadTarget.PROD.styleClass)
                    root.removeClass(UploadTarget.DEV.styleClass)
                }
            }
        }
    }

    private fun initializeAlertDialog() {
        alertDialog {
            alertDialog = this
            uploadTargetProperty.bind(batchDataStore.uploadTargetProperty)
        }
    }

    private fun openAlertDialog(event: AlertDialogEvent) {
        resetConfirmDialog()
        when (event.type) {
            AlertType.INFO -> openInfoDialog(event)
            AlertType.CONFIRM -> openConfirmDialog(event)
        }
    }

    private fun openInfoDialog(event: AlertDialogEvent) {
        alertDialog.apply {
            isWarningProperty.set(event.isWarning)
            titleTextProperty.set(event.title)
            messageTextProperty.set(event.message)
            detailsTextProperty.set(event.details)
            primaryButtonTextProperty.set(event.primaryText)
            setOnPrimaryAction { close() }
            open()
        }
    }

    private fun openConfirmDialog(event: AlertDialogEvent) {
        resetConfirmDialog()
        alertDialog.apply {
            isWarningProperty.set(event.isWarning)
            titleTextProperty.set(event.title)
            messageTextProperty.set(event.message)
            detailsTextProperty.set(event.details)
            primaryButtonTextProperty.set(event.primaryText)
            primaryButtonIconProperty.set(event.primaryIcon)
            secondaryButtonTextProperty.set(event.secondaryText)
            secondaryButtonIconProperty.set(event.secondaryIcon)

            setOnPrimaryAction {
                event.primaryAction()
                close()
            }
            setOnSecondaryAction {
                event.secondaryAction()
                close()
            }
            open()
        }
    }

    private fun resetConfirmDialog() {
        alertDialog.apply {
            isWarningProperty.set(false)
            titleTextProperty.set(null)
            messageTextProperty.set(null)
            detailsTextProperty.set(null)
            primaryButtonTextProperty.set(null)
            primaryButtonIconProperty.set(null)
            onPrimaryActionProperty.set(null)
            secondaryButtonTextProperty.set(null)
            secondaryButtonIconProperty.set(null)
            onSecondaryActionProperty.set(null)
        }
    }

    private fun initializeProgressDialog() {
        progressDialog {
            progressDialog = this
            uploadTargetProperty.bind(batchDataStore.uploadTargetProperty)
        }
    }

    private fun openProgressDialog(event: ProgressDialogEvent) {
        progressDialog.apply {
            progressProperty.unbind()

            titleTextProperty.set(event.title)
            messageTextProperty.set(event.message)
            showProgressBarProperty.set(event.showProgressBar)

            progressProperty.bind(event.progressProperty)

            if (event.show) open() else close()
        }
    }

    private fun initializeLoginDialog() {
        loginDialog {
            loginDialog = this
            uploadTargetProperty.bind(batchDataStore.uploadTargetProperty)
            serverProperty.bindBidirectional(batchDataStore.serverProperty)
            userProperty.bindBidirectional(batchDataStore.userProperty)
            passwordProperty.bindBidirectional(batchDataStore.passwordProperty)

        }
    }

    private fun openLoginDialog(event: LoginDialogEvent) {
        loginDialog.apply {
            titleTextProperty.set(messages["login"])
            messageTextProperty.set(messages["enterCredentials"])
            setOnAction {
                event.action()
                close()
            }
            open()
        }
    }
}