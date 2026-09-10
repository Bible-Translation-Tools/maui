package org.bibletranslationtools.maui.jvm

import javafx.scene.image.Image
import javafx.stage.Stage
import org.bibletranslationtools.maui.common.InitializeApp
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.bibletranslationtools.maui.jvm.di.AppDependencyGraph
import org.bibletranslationtools.maui.jvm.di.DaggerAppDependencyGraph
import org.bibletranslationtools.maui.jvm.di.IDependencyGraphProvider
import org.bibletranslationtools.maui.jvm.ui.events.AppCloseRequestEvent
import org.bibletranslationtools.maui.jvm.ui.AppWorkspace
import tornadofx.*
import javax.inject.Inject

class MainApp : App(AppWorkspace::class), IDependencyGraphProvider {
    override val dependencyGraph: AppDependencyGraph =
        DaggerAppDependencyGraph.builder().build()

    @Inject lateinit var directoryProvider: IDirectoryProvider
    @Inject lateinit var initializeApp: InitializeApp

    init {
        dependencyGraph.inject(this)
        directoryProvider.cleanTempDirectory()
        initializeLogger(directoryProvider)
        initializeApp.initialize().subscribe()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isMaximized = true
        stage.minWidth = 900.0
        stage.minHeight = 700.0

        javaClass.getResource("/launcher.png")?.let { url ->
            stage.icons.add(Image(url.openStream()))
        }
        stage.scene.window.setOnCloseRequest {
            fire(AppCloseRequestEvent())
        }
    }

    private fun initializeLogger(directoryProvider: IDirectoryProvider) {
        ConfigureLogger(
            directoryProvider.logsDirectory
        ).configure()
    }
}

fun main(args: Array<String>) {
    launch<MainApp>(args)
}
