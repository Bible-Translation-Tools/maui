package org.bibletranslationtools.maui.jvm.ui.startup

import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.ui.BatchDataStore
import org.bibletranslationtools.maui.jvm.ui.UploadTarget
import org.bibletranslationtools.maui.jvm.ui.components.mainHeader
import tornadofx.*

class StartupPage : View() {
    private val viewModel: StartupViewModel by inject()
    private val batchDataStore: BatchDataStore by inject()

    init {
        importStylesheet(AppResources.load("/css/startup.css"))
    }

    override val root = borderpane {
        addClass("startup__page")

        top = mainHeader {
            uploadTargetProperty.bind(batchDataStore.uploadTargetProperty)
            appTitleProperty.bind(batchDataStore.appTitleProperty)
        }

        center = vbox {
            addClass("startup__page-main")

            label(messages["appName"]) {
                addClass("app-name__text")
            }

            vbox {
                addClass("upload-target")

                label(messages["chooseUploadTarget"]) {
                    addClass("upload-target__text")
                }

                button(messages["targetDev"]) {
                    addClass(
                        "btn",
                        "btn--primary-inverted",
                        "upload-target__btn",
                        "upload-target__btn--dev"
                    )
                    action {
                        viewModel.selectUploadTarget(UploadTarget.DEV)
                    }
                }

                button(messages["targetProd"]) {
                    addClass(
                        "btn",
                        "btn--primary-inverted",
                        "upload-target__btn",
                        "upload-target__btn--prod"
                    )
                    action {
                        viewModel.selectUploadTarget(UploadTarget.PROD)
                    }
                }
            }
        }
    }

    override fun onDock() {
        viewModel.onDock()
    }

    override fun onUndock() {
        viewModel.onUndock()
    }
}