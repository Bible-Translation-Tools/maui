package org.bibletranslationtools.maui.common.validators

import org.apache.tika.Tika
import java.io.File

class CueValidator(private val file: File) : IValidator {

    companion object {
        private const val PLAIN_TEXT_MIME_TYPE = "text/plain"
        // tika incorrectly detects cue file,
        // which content starts with "REM COMMENT"
        // as x-bat mime type
        private const val X_BAT_MIME_TYPE = "application/x-bat"
    }

    override fun validate() {
        val fileType = Tika().detect(file)

        if (fileType != PLAIN_TEXT_MIME_TYPE && fileType != X_BAT_MIME_TYPE) {
            throw IllegalArgumentException("This doesn't look like a CUE file")
        }
    }
}
