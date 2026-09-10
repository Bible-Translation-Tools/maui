package org.bibletranslationtools.maui.jvm.data

import java.lang.IllegalArgumentException

enum class FileStatusFilter(val filter: String) {
    PROCESSED("processed"),
    REJECTED("rejected"),
    GROUP("groupSimilar"),
    RESET("reset");

    companion object {
        fun of(filter: String) =
            FileStatusFilter.values().singleOrNull {
                it.name == filter.uppercase() || it.filter == filter
            } ?: throw IllegalArgumentException("File Status Filter $filter is not supported")
    }

    override fun toString(): String {
        return filter
    }
}