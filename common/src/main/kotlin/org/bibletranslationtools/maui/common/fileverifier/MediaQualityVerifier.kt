package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.MediaQuality
import org.bibletranslationtools.maui.common.data.VerifiedResult

class MediaQualityVerifier : FileVerifier() {
    override fun verify(media: Media): VerifiedResult {
        return when {
            (media.isCompressed || media.isContainerAndCompressed) && media.mediaQuality == null -> {
                rejected("Media quality needs to be specified for compressed media.")
            }
            !media.isCompressed && !media.isContainerAndCompressed && media.mediaQuality != null -> {
                rejected("Non-compressed media should not have a quality.")
            }
            media.mediaQuality != null && !isSupported(media.mediaQuality) -> {
                rejected("Media quality ${media.mediaQuality} is not supported.")
            }
            else -> processed()
        }
    }

    private fun isSupported(quality: MediaQuality): Boolean {
        return listOf(
            MediaQuality.HI,
            MediaQuality.LOW
        ).contains(quality)
    }
}