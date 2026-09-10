package org.bibletranslationtools.maui.common

import io.reactivex.Completable
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import java.util.zip.ZipInputStream
import javax.inject.Inject


class InitializeApp @Inject constructor(
    private val directoryProvider: IDirectoryProvider
) {
    fun initialize(): Completable {
        return Completable.fromCallable {
            copySoxBinaries()
        }
    }

    private fun copySoxBinaries() {
        javaClass.getResource("/sox.zip")?.openStream()?.let { stream ->
            ZipInputStream(stream).use { zip ->
                val soxTargetDir = directoryProvider.soxBinaryDirectory
                generateSequence { zip.nextEntry }
                    .forEach {
                        if (it.isDirectory) {
                            soxTargetDir.resolve(it.name).mkdirs()
                        } else {
                            val file = soxTargetDir.resolve(it.name)
                            if (!file.exists()) {
                                file.writeBytes(zip.readAllBytes())
                                file.setExecutable(true)
                            }
                        }
                    }
            }
        }
    }
}