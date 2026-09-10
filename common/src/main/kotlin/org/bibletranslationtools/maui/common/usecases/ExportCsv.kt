package org.bibletranslationtools.maui.common.usecases

import io.reactivex.Completable
import org.bibletranslationtools.maui.common.data.Media
import java.io.File
import javax.inject.Inject

class ExportCsv @Inject constructor() {
    fun export(items: List<Media>, output: File): Completable {
        return Completable.fromCallable {
            val header = makeHeader()
            val body = makeBody(items)
            val csv = header + body

            output.createNewFile()
            output.outputStream().use {
                it.write(csv.toByteArray())
            }
        }
    }

    private fun makeHeader(): String {
        val builder = StringBuilder()
        builder.append("selected,")
        builder.append("file name,")
        builder.append("parent file,")
        builder.append("language,")
        builder.append("resource type,")
        builder.append("book,")
        builder.append("chapter,")
        builder.append("media extension,")
        builder.append("media quality,")
        builder.append("grouping,")
        builder.append("status,")
        builder.append("status message\n")

        return builder.toString()
    }

    private fun makeBody(items: List<Media>): String {
        return items.joinToString("\n") { item ->
            val builder = StringBuilder()
            builder.append(if (item.selected) "*" else "")
            builder.append(",")
            builder.append("\"${item.file}\"")
            builder.append(",")
            builder.append("\"${item.parentFile ?: "--"}\"")
            builder.append(",")
            builder.append(item.language ?: "--")
            builder.append(",")
            builder.append(item.resourceType ?: "--")
            builder.append(",")
            builder.append(item.book ?: "--")
            builder.append(",")
            builder.append(item.chapter ?: "--")
            builder.append(",")
            builder.append(item.mediaExtension ?: "--")
            builder.append(",")
            builder.append(item.mediaQuality ?: "--")
            builder.append(",")
            builder.append(item.grouping ?: "--")
            builder.append(",")
            builder.append(item.status ?: "--")
            builder.append(",")
            builder.append(item.statusMessage?.let { "\"$it\"" } ?: "--")
            builder
        }
    }
}