package org.bibletranslationtools.maui.jvm

import javafx.collections.FXCollections
import javafx.collections.transformation.SortedList
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.ScrollBar
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material.Material
import tornadofx.SortedFilteredList
import tornadofx.add
import tornadofx.addClass

fun Parent.customizeScrollbarSkin() {
    val scrollBars = lookupAll(".scroll-bar")
    scrollBars
        .mapNotNull { it as? ScrollBar }
        .forEach { bar ->
            val thumb = bar.lookup(".thumb")
            (thumb as? StackPane)?.let { t ->
                if (t.children.size == 0) {
                    t.add(
                        FontIcon(Material.DRAG_INDICATOR).apply {
                            addClass("thumb-icon")
                            if (bar.orientation == Orientation.HORIZONTAL) rotate = 90.0
                        }
                    )
                }
            }
        }
}

fun <T> TableView<T>.bindSortPolicy() {
    setSortPolicy {
        try {
            val itemsList = items
            if (itemsList is SortedList<*> ||
                itemsList is SortedFilteredList<*> ||
                itemsList == null ||
                itemsList.isEmpty()) {
                return@setSortPolicy true
            } else {
                val comparator = comparator ?: return@setSortPolicy true
                FXCollections.sort(itemsList, comparator)
                return@setSortPolicy true
            }
        } catch (e: UnsupportedOperationException) {
            return@setSortPolicy false
        }
    }
}

fun <S, T> TableColumn<S, T>.bindColumnSortComparator() {
    val list = tableView.items
    sortTypeProperty().onChangeAndDoNow {
        if (list is SortedList<S>) {
            list.comparator = tableView.comparator
        } else if (list is SortedFilteredList<S>) {
            list.sortedItems.comparator = tableView.comparator
        }
    }
}

fun <S> TableView<S>.bindTableSortComparator() {
    val list = this.items
    if (list is SortedList<S>) {
        comparatorProperty().onChangeAndDoNow {
            list.comparator = it
        }
    } else if (list is SortedFilteredList<S>) {
        comparatorProperty().onChangeAndDoNow {
            list.sortedItems.comparator = it
        }
    }
}