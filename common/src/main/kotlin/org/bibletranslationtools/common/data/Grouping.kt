package org.bibletranslationtools.common.data

import java.lang.IllegalArgumentException

enum class Grouping(val grouping: String) {
    BOOK("book"),
    CHAPTER("chapter"),
    CHUNK("chunk"),
    VERSE("verse");

    companion object {
        fun of(grouping: String) =
            values().singleOrNull {
                it.name == grouping.toUpperCase() || it.grouping == grouping
            } ?: throw IllegalArgumentException("There is no grouping $grouping")
    }
}
