package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.MediaExtension
import org.bibletranslationtools.maui.common.data.VerifiedResult

class MediaExtensionVerifier : FileVerifier() {
    override fun verify(media: Media): VerifiedResult {
        return when {
            (media.isContainer || media.isContainerAndCompressed) && media.mediaExtension == null -> {
                rejected("Media extension needs to be specified for container.")
            }
            !media.isContainer && media.mediaExtension != null -> {
                rejected("Media extension cannot be applied to non-container media.")
            }
            media.mediaExtension != null && !isSupported(media.mediaExtension) -> {
                rejected("Media extension ${media.mediaExtension} is not supported.")
            }
            else -> processed()
        }
    }

    private fun isSupported(extension: MediaExtension): Boolean {
        return listOf(
            MediaExtension.WAV,
            MediaExtension.MP3,
            MediaExtension.JPG
        ).contains(extension)
    }
}