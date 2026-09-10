package org.bibletranslationtools.maui.common.validators

import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.wycliffeassociates.io.ArchiveOfHolding
import org.wycliffeassociates.io.LanguageLevel
import java.io.File
import java.util.UUID

class TrValidator(
    private val directoryProvider: IDirectoryProvider,
    private val file: File
) : IValidator {

    /**
     * Validates TR file integrity by trying to extract it
     * @throws Exception Can throw an exception if extraction fails
     */
    override fun validate() {
        try {
            file.inputStream().use { fis ->
                fis.buffered().use { bis ->
                    val ll = LanguageLevel()
                    val aoh = ArchiveOfHolding(bis, ll)
                    val uuid = UUID.randomUUID()
                    val out = directoryProvider.createTempDirectory(uuid.toString())
                    aoh.extractArchive(file, out)
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("TR file is not valid.")
        }
    }
}
