package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.VerifiedResult

class LanguageVerifier(private val languages: List<String>) : FileVerifier() {
    override fun verify(media: Media): VerifiedResult {
        return when {
            media.language.isNullOrBlank() -> {
                rejected("Language should be specified.")
            }
            !languages.contains(media.language) -> {
                rejected("${media.language} is not a valid language.")
            }
            else -> processed()
        }
    }
}