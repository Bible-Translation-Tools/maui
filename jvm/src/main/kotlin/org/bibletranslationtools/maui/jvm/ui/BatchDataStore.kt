package org.bibletranslationtools.maui.jvm.ui

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.persistence.*
import org.bibletranslationtools.maui.jvm.di.IDependencyGraphProvider
import org.bibletranslationtools.maui.jvm.onChangeAndDoNow
import tornadofx.*
import java.util.*
import javax.inject.Inject

enum class UploadTarget(val target: String, val styleClass: String) {
    DEV("dev", "dev-target"),
    PROD("prod", "prod-target")
}

class BatchDataStore : Component(), ScopedInstance {

    @Inject
    lateinit var prefRepository: IPrefRepository

    val appTitleProperty = SimpleStringProperty()
    val uploadTargetProperty = SimpleObjectProperty<UploadTarget>()

    val uploadTargets = observableListOf(UploadTarget.DEV, UploadTarget.PROD)
    val activeBatchProperty = SimpleObjectProperty<Batch>()

    val serverProperty = SimpleStringProperty("")
    val userProperty = SimpleStringProperty("")
    val passwordProperty = SimpleStringProperty("")

    init {
        (app as IDependencyGraphProvider).dependencyGraph.inject(this)

        val version = getVersion()
        val appTitle = messages["appName"] + (if (version == null) "" else " - $version")
        appTitleProperty.set(appTitle)

        uploadTargetProperty.onChangeAndDoNow {
            when (it) {
                UploadTarget.DEV -> {
                    serverProperty.set(prefRepository.get(DEV_SERVER_NAME_KEY))
                    userProperty.set(prefRepository.get(DEV_USER_NAME_KEY))
                    passwordProperty.set("")
                }
                UploadTarget.PROD -> {
                    serverProperty.set(prefRepository.get(PROD_SERVER_NAME_KEY))
                    userProperty.set(prefRepository.get(PROD_USER_NAME_KEY))
                    passwordProperty.set("")
                }
                else -> {}
            }
        }
    }

    private fun getVersion(): String? {
        val prop = Properties()
        val inputStream = javaClass.classLoader.getResourceAsStream("version.properties")

        if (inputStream != null) {
            prop.load(inputStream)
            return prop.getProperty("version")
        }

        return null
    }
}