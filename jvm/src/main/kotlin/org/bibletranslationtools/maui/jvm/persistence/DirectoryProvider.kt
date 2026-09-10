package org.bibletranslationtools.maui.jvm.persistence

import org.bibletranslationtools.maui.common.MauiInfo
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import java.io.File
import java.nio.file.FileSystems

class DirectoryProvider(private val appName: String) : IDirectoryProvider {
    private val pathSeparator = FileSystems.getDefault().separator
    private val userHome = System.getProperty("user.home")
    private val windowsAppData = System.getenv("APPDATA")
    private val osName = System.getProperty("os.name").uppercase()

    override val logsDirectory: File
        get() = getAppDataDirectory("logs")

    override val tempDirectory: File
        get() = getAppDataDirectory("temp")

    override val batchDirectory: File
        get() = getAppDataDirectory("batches")

    override val cacheDirectory: File
        get() = getAppDataDirectory("cache")

    override val soxBinaryDirectory: File
        get() = getAppDataDirectory("sox")

    override val prefFile: File
        get() = getAppDataDirectory().resolve("${MauiInfo.APP_NAME.lowercase()}.properties")

    override fun getAppDataDirectory(appendedPath: String): File {
        val pathComponents = mutableListOf<String>()

        when {
            osName.contains("WIN") -> pathComponents.add(windowsAppData)
            osName.contains("MAC") -> {
                // use /Users/<user>/Library/Application Support/ for macOS
                pathComponents.add(userHome)
                pathComponents.add("Library")
                pathComponents.add("Application Support")
            }

            osName.contains("LINUX") -> {
                pathComponents.add(userHome)
                pathComponents.add(".config")
            }
        }

        pathComponents.add(appName)

        if (appendedPath.isNotEmpty()) pathComponents.add(appendedPath)

        // create the directory if it does not exist
        val pathString = pathComponents.joinToString(pathSeparator)
        val file = File(pathString)
        file.mkdirs()
        return file
    }

    override fun createCacheDirectory(dirName: String): File {
        val dir = cacheDirectory.resolve(dirName)
        dir.mkdirs()
        return dir
    }

    override fun createTempDirectory(dirName: String): File {
        val dir = tempDirectory.resolve(dirName)
        dir.mkdirs()
        return dir
    }

    override fun createTempFile(prefix: String, suffix: String?): File {
        return File.createTempFile(prefix, suffix, tempDirectory)
    }

    override fun cleanTempDirectory() {
        deleteRecursively(tempDirectory)
    }

    override fun deleteCachedFiles(files: List<File>) {
        files.forEach { file ->
            // delete only files that reside in cache directory
            val parentDir = file.parentFile
            val isCached = file.absolutePath.startsWith(
                cacheDirectory.absolutePath
            )
            if (file.exists() && isCached) {
                parentDir.deleteRecursively()
            }
        }
    }

    private fun deleteRecursively(dir: File) {
        dir.listFiles()?.forEach {
            if (it.isDirectory) {
                deleteRecursively(it)
            }
            it.delete()
        }
    }
}