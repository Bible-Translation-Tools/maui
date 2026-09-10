package org.bibletranslationtools.maui.jvm.controls.batchtableview

import javafx.scene.control.TableCell
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.jvm.ui.events.EditBatchNameEvent
import tornadofx.FX

class EditableNameCell : TableCell<Batch, Batch>() {
    private val batchNameView = BatchNameView()

    override fun updateItem(item: Batch?, empty: Boolean) {
        super.updateItem(item, empty)

        if (item == null || empty) {
            text = item?.name
            graphic = null
        } else {
            graphic = batchNameView.apply {
                nameProperty.set(item.name)

                setOnEdit {
                    editingProperty.set(true)
                }

                setOnSave {
                    editingProperty.set(false)
                    FX.eventbus.fire(EditBatchNameEvent(item, nameProperty.value))
                }
            }
        }
    }
}