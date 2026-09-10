package org.bibletranslationtools.maui.jvm.controls.batchtableview

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.bindColumnSortComparator
import org.bibletranslationtools.maui.jvm.bindSortPolicy
import org.bibletranslationtools.maui.jvm.bindTableSortComparator
import org.bibletranslationtools.maui.jvm.customizeScrollbarSkin
import org.bibletranslationtools.maui.jvm.ui.events.DeleteBatchEvent
import org.bibletranslationtools.maui.jvm.ui.events.OpenBatchEvent
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material.Material
import org.kordamp.ikonli.materialdesign.MaterialDesign
import tornadofx.*
import tornadofx.FX.Companion.messages
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BatchTableView(
    batches: ObservableList<Batch>
) : TableView<Batch>(batches) {

    init {
        addClass("batch-table-view")
        importStylesheet(AppResources.load("/css/batch-table-view.css"))

        runLater { customizeScrollbarSkin() }

        vgrow = Priority.ALWAYS
        columnResizePolicy = CONSTRAINED_RESIZE_POLICY

        placeholder = borderpane {
            center = vbox {
                addClass("placeholder")

                label {
                    addClass("placeholder-icon")
                    graphic = FontIcon(MaterialDesign.MDI_FOLDER_OUTLINE)
                }
                label(messages["noBatchesPrompt"]) {
                    addClass("placeholder-text")
                }
            }
        }

        setRowFactory {
            TableRow<Batch>().apply {
                setOnMouseClicked {
                    if (!isEmpty) {
                        FX.eventbus.fire(OpenBatchEvent(item))
                    }
                }
            }
        }

        bindSortPolicy()
        bindTableSortComparator()

        column(messages["batchName"], Batch::class) {
            setCellValueFactory {
                SimpleObjectProperty(it.value)
            }
            setCellFactory {
                EditableNameCell()
            }
            setComparator { o1, o2 ->
                o1.name.compareTo(o2.name, ignoreCase = true)
            }
            bindColumnSortComparator()
            isReorderable = false
        }

        column(messages["batchDate"], String::class) {
            setCellValueFactory {
                formatDateTime(it.value.created)
            }
            bindColumnSortComparator()
            comparator = compareByDescending<String> { it }
            isReorderable = false
        }

        column("", Node::class) {
            minWidth = 48.0
            maxWidth = 48.0

            setCellValueFactory {
                button {
                    addClass("btn", "btn--icon", "btn--delete")

                    graphic = FontIcon(Material.DELETE_OUTLINE)
                    tooltip(messages["deleteBatch"])

                    action {
                        FX.eventbus.fire(DeleteBatchEvent(it.value))
                    }
                }.toProperty()
            }
            isReorderable = false
            isSortable = false
        }
    }

    private fun formatDateTime(dateTime: String): ObservableValue<String> {
        val parsed = LocalDateTime.parse(dateTime)
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a")
        return parsed.format(formatter).toProperty()
    }
}

fun EventTarget.batchTableView(
    values: ObservableList<Batch>,
    op: BatchTableView.() -> Unit = {}
) = BatchTableView(values).attachTo(this, op)
