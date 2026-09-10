package org.bibletranslationtools.maui.common.data

import java.io.File

class FileResult(
    val file: File,
    val status: FileStatus? = null,
    val statusMessage: String? = null,
    val data: Media? = null
)