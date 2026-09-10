package org.bibletranslationtools.maui.common.usecases

import org.bibletranslationtools.maui.common.data.*
import org.bibletranslationtools.maui.common.fileverifier.*
import org.bibletranslationtools.maui.common.io.ILanguagesReader
import org.bibletranslationtools.maui.common.io.IResourceTypesReader
import org.bibletranslationtools.maui.common.io.IVersificationReader
import javax.inject.Inject

class FileVerifyingRouter @Inject constructor(
    versificationReader: IVersificationReader,
    languagesReader: ILanguagesReader,
    resourceTypesReader: IResourceTypesReader
) {

    private val versification = versificationReader.read()
    private val languages = languagesReader.read()
    private val resourceTypes = resourceTypesReader.read()
    private val verifiers = getVerifiers()

    fun handleMedia(media: Media): VerifiedResult {
        return verifiers
            .map {
                it.verify(media)
            }
            .firstOrNull {
                it.status == FileStatus.REJECTED
            } ?: VerifiedResult(FileStatus.PROCESSED)
    }

    private fun getVerifiers(): List<FileVerifier> {
        return listOf(
            LanguageVerifier(languages),
            ResourceTypeVerifier(resourceTypes),
            BookVerifier(versification),
            ChapterVerifier(versification),
            MediaExtensionVerifier(),
            MediaQualityVerifier(),
            GroupingVerifier(),
            ContentVerifier(versification)
        )
    }
}