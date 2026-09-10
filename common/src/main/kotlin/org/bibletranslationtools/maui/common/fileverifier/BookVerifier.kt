package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.VerifiedResult
import org.bibletranslationtools.maui.common.io.Versification

class BookVerifier(private val versification: Versification) : FileVerifier() {
    override fun verify(media: Media): VerifiedResult {
        val book = media.book?.uppercase()
        return when {
            book == null -> {
                rejected("Book should be specified.")
            }
            !versification.contains(book) -> {
                rejected("${media.book} is not a valid book.")
            }
            media.grouping == Grouping.BOOK && media.chapter != null -> {
                rejected("File with grouping \"${Grouping.BOOK}\" must not have chapter.")
            }
            else -> processed()
        }
    }
}