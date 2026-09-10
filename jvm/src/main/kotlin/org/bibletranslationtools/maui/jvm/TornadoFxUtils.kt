package org.bibletranslationtools.maui.jvm

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Tab
import javafx.scene.control.TableView
import tornadofx.onChange

interface ListenerDisposer {
    fun dispose()
}

/**
 * Runs the given operation now and also calls tornadofx's [onChange] with the given operation to set up an
 * on change listener
 */
fun <T> ObservableValue<T>.onChangeAndDoNow(op: (T?) -> Unit) {
    op(this.value)
    this.onChange {
        op(it)
    }
}

/**
 * Runs the given operation now and also calls tornadofx's [onChange] with the given operation to set up an
 * on change listener
 */
fun <T> ObservableList<T>.onChangeAndDoNow(op: (List<T>) -> Unit) {
    op(this)
    this.onChange {
        op(it.list)
    }
}

/**
 * Sets up an on change listener to run [op] function
 * @param op the function to run when observable value is changed
 * @return ListenerDisposer
 */
fun <T> ObservableValue<T>.onChangeWithDisposer(op: (T?) -> Unit): ListenerDisposer {
    val listener = ChangeListener<T> { _, _, newValue -> op(newValue) }
    addListener(listener)
    return object : ListenerDisposer {
        override fun dispose() {
            removeListener(listener)
        }
    }
}

/**
 * Sets up an on change listener to run [op] function
 * @param op the function to run when observable list is changed
 * @return ListenerDisposer
 */
fun <T> ObservableList<T>.onChangeWithDisposer(op: (ListChangeListener.Change<out T>) -> Unit): ListenerDisposer {
    val listener = ListChangeListener<T> { op(it) }
    addListener(listener)
    return object : ListenerDisposer {
        override fun dispose() {
            removeListener(listener)
        }
    }
}

/**
 * Runs the given operation now and also calls tornadofx's [onChange] with the given operation to set up an
 * on change listener
 * @param op the function to run now and when observable list is changed
 * @return ListenerDisposer
 */
fun <T> ObservableList<T>.onChangeAndDoNowWithDisposer(op: (List<T>) -> Unit): ListenerDisposer {
    op(this)
    val listener = ListChangeListener<T> { op(it.list) }
    addListener(listener)
    return object : ListenerDisposer {
        override fun dispose() {
            removeListener(listener)
        }
    }
}

/**
 * Runs the given operation now and also calls [onChangeWithDisposer] with the given operation to set up an
 * on change listener
 * @param op the function to run when observable value is changed
 * @return ListenerDisposer
 */
fun <T> ObservableValue<T>.onChangeAndDoNowWithDisposer(op: (T?) -> Unit): ListenerDisposer {
    op(this.value)
    return this.onChangeWithDisposer {
        op(it)
    }
}

/**
 * Runs an [op] function when tab is selected
 * @param op the function to run when tab is selected
 * @return ListenerDisposer
 */
fun Tab.whenSelectedWithDisposer(op: () -> Unit): ListenerDisposer {
    return selectedProperty().onChangeWithDisposer { if (it == true) op() }
}

fun <S> TableView<S>.onSelectionChangeWithDisposer(op: (S?) -> Unit): ListenerDisposer {
    return selectionModel.selectedItemProperty().onChangeWithDisposer { op(it) }
}