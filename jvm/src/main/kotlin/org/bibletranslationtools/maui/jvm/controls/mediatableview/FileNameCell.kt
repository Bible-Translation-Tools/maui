package org.bibletranslationtools.maui.jvm.controls.mediatableview

import javafx.scene.control.TableCell
import org.bibletranslationtools.maui.jvm.data.MediaItem
import tornadofx.FX.Companion.messages
import tornadofx.get
import tornadofx.label
import tornadofx.tooltip
import java.text.MessageFormat

class FileNameCell : TableCell<MediaItem, MediaItem>() {
    private val label = label()

    override fun updateItem(item: MediaItem?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty) {
            graphic = null
            return
        }

        graphic = label.apply {
            text = item?.file?.name
            tooltip {
                item?.file?.absolutePath?.let { path ->
                    val parent = item.parentFile?.absolutePath?.let {
                        MessageFormat.format(
                            messages["parentFileInfo"],
                            it
                        )
                    } ?: ""
                    text = "$path\n$parent"
                }
            }
        }
    }
}