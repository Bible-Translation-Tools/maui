package org.bibletranslationtools.maui.jvm.controls.dialog

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Bounds
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.bibletranslationtools.maui.jvm.assets.AppResources
import org.bibletranslationtools.maui.jvm.onChangeAndDoNow
import org.bibletranslationtools.maui.jvm.ui.UploadTarget
import tornadofx.*

abstract class MauiDialog : Fragment() {
    val uploadTargetProperty = SimpleObjectProperty<UploadTarget>()
    val titleTextProperty = SimpleStringProperty()
    val messageTextProperty = SimpleStringProperty()
    val detailsTextProperty = SimpleStringProperty()

    private val roundRadius = 15.0
    private val mainContainer = VBox().apply {
        addClass("main-container")
        vgrow = Priority.ALWAYS
    }

    override val root = VBox().apply {
        addClass("maui-dialog")
        add(mainContainer)
    }

    init {
        importStylesheet(AppResources.load("/css/maui-dialog.css"))

        bindUploadTargetClassToRoot()
    }

    fun open() {
        val stage = openModal(StageStyle.TRANSPARENT, Modality.APPLICATION_MODAL, false)
        stage?.let { _stage ->
            fitStageToParent(_stage)
        }
    }

    fun setContent(content: Region) {
        mainContainer.add(
            content.apply {
                addClass("content")

                vgrow = Priority.NEVER
                maxWidth = Region.USE_PREF_SIZE

                layoutBoundsProperty().onChange {
                    it?.let {
                        clipRegion(content, it)
                    }
                }
            }
        )
    }

    private fun fitStageToParent(stage: Stage) {
        stage.width = primaryStage.width
        stage.height = primaryStage.height
        stage.x = primaryStage.x
        stage.y = primaryStage.y
        stage.scene.fill = Color.TRANSPARENT
    }

    private fun clipRegion(region: Region, bounds: Bounds) {
        val rect = Rectangle()
        rect.width = bounds.width
        rect.height = bounds.height
        rect.arcWidth = roundRadius
        rect.arcHeight = roundRadius
        region.clip = rect
    }

    private fun bindUploadTargetClassToRoot() {
        uploadTargetProperty.onChangeAndDoNow {
            when (it) {
                UploadTarget.DEV -> {
                    root.addClass(UploadTarget.DEV.styleClass)
                    root.removeClass(UploadTarget.PROD.styleClass)
                }
                else -> {
                    root.addClass(UploadTarget.PROD.styleClass)
                    root.removeClass(UploadTarget.DEV.styleClass)
                }
            }
        }
    }
}
