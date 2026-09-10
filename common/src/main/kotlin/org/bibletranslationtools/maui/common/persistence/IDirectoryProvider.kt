package org.bibletranslationtools.maui.common.persistence

import java.io.File

interface IDirectoryProvider {
    val logsDirectory: File
    val tempDirectory: File
    val batchDirectory: File
    val cacheDirectory: File
    val soxBinaryDirectory: File
    val prefFile: File

    /** Directory to store the application's private data */
    fun getAppDataDirectory(appendedPath: String = ""): File

    /** Create directory in cache directory */
    fun createCacheDirectory(dirName: String): File

    /** Create directory in temporary directory */
    fun createTempDirectory(dirName: String): File

    /** Create temp file */
    fun createTempFile(prefix: String, suffix: String? = null): File

    /** Clean temporary directory */
    fun cleanTempDirectory()

    /** Find and delete cached files in the provided list */
    fun deleteCachedFiles(files: List<File>)
}