package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.Grouping
import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.VerifiedResult

class GroupingVerifier : FileVerifier() {
    override fun verify(media: Media): VerifiedResult {
        return when {
            media.grouping == null -> {
                rejected("Grouping needs to be specified.")
            }
            !isSupported(media.grouping) -> {
                rejected("Grouping ${media.grouping} is not supported.")
            }
            else -> processed()
        }
    }

    private fun isSupported(grouping: Grouping): Boolean {
        return listOf(
            Grouping.BOOK,
            Grouping.CHAPTER,
            Grouping.CHUNK,
            Grouping.VERSE
        ).contains(grouping)
    }
}