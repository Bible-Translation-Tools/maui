package org.bibletranslationtools.maui.jvm.ui.batch

import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.jvm.ListenerDisposer
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.controls.textfield.SearchBar
import org.bibletranslationtools.maui.jvm.controls.batchtableview.batchTableView
import org.bibletranslationtools.maui.jvm.controls.textfield.searchBar
import org.bibletranslationtools.maui.jvm.ui.DialogViewModel
import org.bibletranslationtools.maui.jvm.ui.UploadTarget
import org.bibletranslationtools.maui.jvm.ui.components.mainHeader
import org.bibletranslationtools.maui.jvm.ui.components.uploadTargetHeader
import org.bibletranslationtools.maui.jvm.ui.events.*
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material.Material
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*

class BatchPage : View() {
    private val viewModel: BatchViewModel by inject()
    private val dialogViewModel: DialogViewModel by inject()

    private val listeners = mutableListOf<ListenerDisposer>()

    private lateinit var batchContent: VBox
    private lateinit var tableView: TableView<Batch>
    private lateinit var searchBar: SearchBar

    init {
        importStylesheet(AppResources.load("/css/batch-page.css"))

        subscribe<OpenBatchEvent> {
            viewModel.openBatch(it.batch)
        }

        subscribe<DeleteBatchEvent> {
            deleteBatch(it.batch)
        }

        subscribe<EditBatchNameEvent> {
            viewModel.editBatchName(it.batch, it.name)
        }
    }

    override val root = borderpane {
        top = mainHeader {
            uploadTargetProperty.bind(viewModel.uploadTargetProperty)
            appTitleProperty.bind(viewModel.appTitleProperty)
        }

        center = vbox {
            addClass("batch-page")

            batchContent = this

            uploadTargetHeader {
                uploadTargetProperty.bindBidirectional(viewModel.uploadTargetProperty)
                Bindings.bindContent(uploadTargets, viewModel.uploadTargets)

                uploadTargetTextProperty.bind(viewModel.uploadTargetProperty.stringBinding {
                    when (it) {
                        UploadTarget.DEV -> messages["targetDev"]
                        UploadTarget.PROD -> messages["targetProd"]
                        else -> ""
                    }
                })
            }

            vbox {
                addClass("contents")

                vgrow = Priority.ALWAYS

                hbox {
                    addClass("controls")

                    button(messages["importFiles"]) {
                        addClass("btn", "btn--primary")
                        graphic = FontIcon(MaterialDesign.MDI_DOWNLOAD)

                        action {
                            chooseFile(
                                messages["importFiles"],
                                arrayOf(),
                                mode = FileChooserMode.Multi,
                                owner = currentWindow
                            ).also { viewModel.onDropFiles(it) }
                        }
                    }
                }

                vbox {
                    addClass("import")

                    label {
                        addClass("import-icon")
                        graphic = FontIcon(MaterialDesign.MDI_DOWNLOAD)
                    }

                    label(messages["importFilesStartProject"]) {
                        addClass("import-title")
                    }

                    label(messages["dropFiles"]) {
                        addClass("import-subtitle")
                    }

                    setOnDragExited {
                        togglePseudoClass("drag-over", false)
                    }

                    setOnDragOver {
                        if (it.dragboard.hasFiles()) {
                            togglePseudoClass("drag-over", true)
                        }
                        onDragOverHandler().handle(it)
                    }
                    onDragDropped = onDragDroppedHandler()
                }

                hbox {
                    addClass("search")

                    label(messages["batches"]) {
                        addClass("search-title")
                    }

                    region {
                        hgrow = Priority.ALWAYS
                    }

                    searchBar {
                        searchBar = this
                        promptText = messages["search"]
                        viewModel.searchQueryProperty.bind(textProperty())
                    }
                }

                batchTableView(viewModel.sortedBatches) {
                    tableView = this
                }
            }
        }
    }

    override fun onDock() {
        viewModel.onDock()
    }

    override fun onUndock() {
        searchBar.text = ""
        listeners.forEach(ListenerDisposer::dispose)
        listeners.clear()
        tableView.selectionModel.select(-1)
        viewModel.onUndock()
    }

    private fun onDragOverHandler(): EventHandler<DragEvent> {
        return EventHandler {
            if (it.gestureSource != this && it.dragboard.hasFiles()) {
                it.acceptTransferModes(TransferMode.COPY)
            }
            it.consume()
        }
    }

    private fun onDragDroppedHandler(): EventHandler<DragEvent> {
        return EventHandler {
            var success = false
            if (it.dragboard.hasFiles()) {
                viewModel.onDropFiles(it.dragboard.files)
                success = true
            }
            it.isDropCompleted = success
            it.consume()
        }
    }

    private fun deleteBatch(batch: Batch) {
        dialogViewModel.showConfirm(
            title = messages["deleteBatch"],
            message = messages["deleteBatchWarning"],
            details = messages["wishToContinue"],
            primaryText = messages["cancel"],
            primaryIcon = FontIcon(MaterialDesign.MDI_CLOSE_CIRCLE),
            secondaryText = messages["delete"],
            secondaryIcon = FontIcon(Material.DELETE_OUTLINE),
            secondaryAction = {
                viewModel.deleteBatch(batch)
            },
            isWarning = true,
        )
    }
}