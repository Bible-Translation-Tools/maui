package org.bibletranslationtools.maui.jvm.ui

import org.bibletranslationtools.maui.jvm.data.MediaItem
import tornadofx.*

class MediaItemComparator : Comparator<MediaItem> {
    private val numRegex = Regex("^\\d+")
    private val letterRegex = Regex("^\\D+")

    override fun compare(o1: MediaItem, o2: MediaItem): Int {
        val o1String = o1.file.name
        val o2String = o2.file.name

        val o1Len = o1String.length
        val o2Len = o2String.length

        var holder1 = o1String
        var holder2 = o2String

        while (holder1.isNotEmpty() && holder2.isNotEmpty()) {
            val str1 = getFirstPart(holder1)
            val str2 = getFirstPart(holder2)

            val result = if (str1.isInt() && str2.isInt()) {
                str1.toInt().compareTo(str2.toInt())
            } else {
                str1.compareTo(str2)
            }

            if (result != 0) return result

            holder1 = holder1.removePrefix(str1)
            holder2 = holder2.removePrefix(str2)
        }

        return o1Len - o2Len
    }

    private fun getFirstPart(str: String): String {
        return if (startsWithDigit(str)) {
            numRegex.find(str)?.value ?: ""
        } else {
            letterRegex.find(str)?.value ?: ""
        }
    }

    private fun startsWithDigit(str: String): Boolean {
        return numRegex.containsMatchIn(str)
    }
}
