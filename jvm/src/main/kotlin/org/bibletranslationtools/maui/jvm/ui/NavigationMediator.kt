package org.bibletranslationtools.maui.jvm.ui

import javafx.application.Platform
import org.bibletranslationtools.maui.jvm.ui.events.AppCloseRequestEvent
import org.bibletranslationtools.maui.jvm.ui.events.AppSaveDoneEvent
import org.bibletranslationtools.maui.jvm.ui.events.AppSaveRequestEvent
import org.bibletranslationtools.maui.jvm.ui.events.NavigationRequestEvent
import tornadofx.*
import kotlin.system.exitProcess

class NavigationMediator : Component(), ScopedInstance {

    private var appExitRequested = false

    init {
        subscribe<NavigationRequestEvent> {
            dock(it.view)
        }
        subscribe<AppCloseRequestEvent> {
            appExitRequested = true
            fire(AppSaveRequestEvent())
        }
        subscribe<AppSaveDoneEvent> {
            if (appExitRequested) {
                runLater {
                    Platform.exit()
                    exitProcess(0)
                }
            }
        }
    }

    inline fun <reified T : UIComponent> dock() {
        val view = find<T>()
        dock(view)
    }

    fun dock(view: UIComponent) {
        if (workspace.dockedComponent != view) {
            workspace.dock(view)
        }
    }
}