package org.bibletranslationtools.maui.jvm.ui.events

import tornadofx.FXEvent
import tornadofx.View

class NavigationRequestEvent(val view: View) : FXEvent()
class AppCloseRequestEvent : FXEvent()
class AppSaveRequestEvent : FXEvent()
class AppSaveDoneEvent : FXEvent()