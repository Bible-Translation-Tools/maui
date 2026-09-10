package org.bibletranslationtools.maui.jvm.ui.events

import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.data.Media
import tornadofx.FXEvent

class OpenBatchEvent(val batch: Batch) : FXEvent()
class DeleteBatchEvent(val batch: Batch) : FXEvent()
class EditBatchNameEvent(val batch: Batch, val name: String) : FXEvent()
class ErrorOccurredEvent(val message: String) : FXEvent()
class ShowInfoEvent(val message: String) : FXEvent()
class BatchMediaUpdatedEvent(val media: List<Media>) : FXEvent()
class ChangeMediaValueEvent(val onConfirm: () -> Unit = {}) : FXEvent()