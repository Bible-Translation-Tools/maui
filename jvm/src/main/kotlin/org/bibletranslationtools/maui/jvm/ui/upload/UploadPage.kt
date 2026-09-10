package org.bibletranslationtools.maui.jvm.ui.upload

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.controls.mediatableview.mediaTableView
import org.bibletranslationtools.maui.jvm.ui.DialogViewModel
import org.bibletranslationtools.maui.jvm.ui.UploadTarget
import org.bibletranslationtools.maui.jvm.ui.components.mainHeader
import org.bibletranslationtools.maui.jvm.ui.components.uploadTargetHeader
import org.bibletranslationtools.maui.jvm.ui.events.*
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material.Material
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UploadPage : View() {

    private val viewModel: UploadMediaViewModel by inject()
    private val dialogViewModel: DialogViewModel by inject()

    private lateinit var nameLabel: Label
    private lateinit var nameTextEdit: TextField
    private val nameEditingProperty = SimpleBooleanProperty()

    init {
        importStylesheet(AppResources.load("/css/upload-page.css"))

        subscribe<AppSaveRequestEvent> {
            viewModel.saveBatch()
        }

        subscribe<ErrorOccurredEvent> {
            dialogViewModel.showError(messages["errorOccurred"], it.message)
        }

        subscribe<ShowInfoEvent> {
            dialogViewModel.showError(
                messages["needsReview"],
                messages["needsReviewMessage"],
                it.message
            )
        }

        subscribe<BatchMediaUpdatedEvent> {
            viewModel.onNewMedia(it.media)
        }

        subscribe<ChangeMediaValueEvent> {
            dialogViewModel.showConfirm(
                title = messages["confirmSelection"],
                message = messages["confirmSelectionQuestion"],
                primaryText = messages["yes"],
                primaryAction = { it.onConfirm() },
                secondaryText = messages["no"]
            )
        }
    }

    override val root = borderpane {
        top = mainHeader {
            uploadTargetProperty.bind(viewModel.uploadTargetProperty)
            appTitleProperty.bind(viewModel.appTitleProperty)
        }

        center = vbox {
            addClass("upload-page")

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
                addClass("root-container")

                vgrow = Priority.ALWAYS

                hbox {
                    addClass("controls")

                    vbox {
                        spacing = 4.0
                        hgrow = Priority.ALWAYS

                        hbox {
                            label {
                                addClass("controls-title")
                                hgrow = Priority.ALWAYS
                                nameLabel = this

                                textProperty().bind(viewModel.batchNameProperty)
                                graphic = FontIcon(MaterialDesign.MDI_PENCIL)
                                contentDisplay = ContentDisplay.RIGHT

                                setOnMouseClicked {
                                    nameEditingProperty.set(true)
                                }

                                visibleProperty().bind(nameEditingProperty.not())
                                managedProperty().bind(visibleProperty())
                            }
                            hbox {
                                addClass("name-edit")
                                hgrow = Priority.ALWAYS

                                textfield {
                                    addClass("name-edit-input")
                                    hgrow = Priority.ALWAYS

                                    nameTextEdit = this
                                    nameEditingProperty.onChange { editing ->
                                        if (editing) {
                                            requestFocus()
                                            selectAll()
                                        }
                                    }

                                    textProperty().bindBidirectional(viewModel.batchNameProperty)
                                    maxWidthProperty().bind(nameLabel.widthProperty())

                                    action {
                                        nameEditingProperty.set(false)
                                    }
                                }
                                button(messages["save"]) {
                                    addClass("btn", "btn--icon", "btn--edit")
                                    graphic = FontIcon(MaterialDesign.MDI_CHECK)

                                    action {
                                        nameEditingProperty.set(false)
                                    }
                                }

                                visibleProperty().bind(nameEditingProperty)
                                managedProperty().bind(visibleProperty())
                            }
                        }

                        label {
                            addClass("controls-subtitle")
                            textProperty().bind(viewModel.activeBatchProperty.stringBinding {
                                it?.let { batch ->
                                    val parsed = LocalDateTime.parse(batch.created)
                                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a")
                                    parsed.format(formatter)
                                } ?: ""
                            })
                        }
                    }

                    region {
                        hgrow = Priority.ALWAYS
                    }

                    button(messages["saveBatch"]) {
                        addClass("btn", "btn--primary")
                        graphic = FontIcon(MaterialDesign.MDI_CONTENT_SAVE)

                        action {
                            viewModel.saveBatch()
                        }

                        enableWhen(viewModel.shouldSaveProperty)
                    }

                    button(messages["importFiles"]) {
                        addClass("btn", "btn--secondary")
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

                    button(messages["viewUploadedFiles"]) {
                        addClass("btn", "btn--secondary")
                        graphic = FontIcon(MaterialDesign.MDI_EXPORT)

                        action {
                            viewModel.viewUploadedFiles()
                        }

                        disableProperty().bind(viewModel.uploadedProperty.not())
                    }

                    button(messages["exportCsv"]) {
                        addClass("btn", "btn--secondary")
                        graphic = FontIcon(MaterialDesign.MDI_FILE_EXPORT)

                        action {
                            val filename = viewModel.activeBatchProperty.value?.let {
                                val parsed = LocalDateTime.parse(it.created)
                                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy_hh-mm-ss-a")
                                "${it.name}_${parsed.format(formatter).lowercase()}"
                            }
                            chooseFile(
                                title = messages["exportCsv"],
                                filters = arrayOf(
                                    FileChooser.ExtensionFilter(
                                        messages["csvFiles"],
                                        "*.csv"
                                    )
                                ),
                                initialFileName = filename,
                                mode = FileChooserMode.Save
                            ).also {
                                it.singleOrNull()?.let { file ->
                                    viewModel.exportCsv(file)
                                }
                            }
                        }

                        enableWhen {
                            viewModel.filteredMediaItems.booleanBinding {
                                it.isNotEmpty()
                            }
                        }
                    }
                }

                mediaTableView(viewModel.filteredMediaItems) {
                    languagesProperty.set(viewModel.languages)
                    resourceTypesProperty.set(viewModel.resourceTypes)
                    booksProperty.set(viewModel.books)
                    mediaExtensionsProperty.set(viewModel.mediaExtensions)
                    mediaQualitiesProperty.set(viewModel.mediaQualities)
                    groupingsProperty.set(viewModel.groupings)
                    statusFilterProperty.set(viewModel.statusFilter)
                    defaultPredicateProperty.set(viewModel.defaultPredicate)
                }
            }
        }

        bottom = hbox {
            addClass("upload-footer")

            button(messages["removeSelected"]) {
                addClass("btn", "btn--secondary")
                graphic = FontIcon(Material.DELETE_OUTLINE)

                action {
                    viewModel.removeSelected()
                }

                enableWhen {
                    viewModel.filteredMediaItems.booleanBinding {
                        it.any { item -> item.selected }
                    }
                }
            }

            region {
                hgrow = Priority.ALWAYS
            }

            button(messages["verify"]) {
                addClass("btn", "btn--secondary")
                graphic = FontIcon(MaterialDesign.MDI_CHECK)

                action {
                    viewModel.verify()
                }

                enableWhen {
                    viewModel.filteredMediaItems.booleanBinding {
                        it.any { item -> item.selected }
                    }
                }
            }

            button(messages["upload"]) {
                addClass("btn", "btn--primary")
                graphic = FontIcon(MaterialDesign.MDI_ARROW_UP)

                action {
                    viewModel.tryUpload()
                }

                enableWhen {
                    viewModel.filteredMediaItems.booleanBinding {
                        it.any { item -> item.selected }
                    }
                }
            }
        }
    }

    override fun onDock() {
        viewModel.onDock()
        nameEditingProperty.set(false)
    }

    override fun onUndock() {
        viewModel.onUndock()
    }
}