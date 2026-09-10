package org.bibletranslationtools.maui.jvm.client

import io.reactivex.Completable
import org.bibletranslationtools.maui.common.client.IFileTransferClient
import java.io.File
import java.lang.IllegalArgumentException

class FileTransferClient(
    private val source: File,
    private val target: File
) : IFileTransferClient {

    override fun transfer(): Completable {
        return Completable.fromCallable {
            if (source.isDirectory) throw IllegalArgumentException("Source should not be a directory")

            source.copyTo(target, true)
        }
    }
}
