package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.FileStatus
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.VerifiedResult

abstract class FileVerifier {
    abstract fun verify(media: Media) : VerifiedResult

    fun rejected(message: String) : VerifiedResult {
        return VerifiedResult(FileStatus.REJECTED, message)
    }

    fun processed() : VerifiedResult {
        return VerifiedResult(FileStatus.PROCESSED)
    }
}