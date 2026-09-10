package org.bibletranslationtools.maui.jvm.ui.startup

import org.bibletranslationtools.maui.jvm.ui.BatchDataStore
import org.bibletranslationtools.maui.jvm.ui.NavigationMediator
import org.bibletranslationtools.maui.jvm.ui.UploadTarget
import org.bibletranslationtools.maui.jvm.ui.batch.BatchPage
import tornadofx.ViewModel

class StartupViewModel : ViewModel() {

    private val navigator: NavigationMediator by inject()
    private val batchDataStore: BatchDataStore by inject()

    fun selectUploadTarget(target: UploadTarget) {
        batchDataStore.uploadTargetProperty.set(target)
        navigator.dock<BatchPage>()
    }

    fun onDock() {
        batchDataStore.activeBatchProperty.set(null)
        batchDataStore.uploadTargetProperty.set(null)
    }

    fun onUndock() {
    }
}