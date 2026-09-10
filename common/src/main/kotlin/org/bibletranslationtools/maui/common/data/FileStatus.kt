package org.bibletranslationtools.maui.common.data

import java.lang.IllegalArgumentException

enum class FileStatus(val status: String) {
    PROCESSED("processed"),
    REJECTED("rejected");

    companion object {
        fun of(status: String) =
            FileStatus.values().singleOrNull {
                it.name == status.uppercase() || it.status == status
            } ?: throw IllegalArgumentException("File Status $status is not supported")
    }

    override fun toString(): String {
        return status
    }
}