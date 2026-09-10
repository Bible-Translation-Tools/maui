package org.bibletranslationtools.maui.common.fileverifier

import org.bibletranslationtools.maui.common.data.Media
import org.bibletranslationtools.maui.common.data.VerifiedResult

class ResourceTypeVerifier(private val resourceTypes: List<String>) : FileVerifier() {
    override fun verify(media: Media): VerifiedResult {
        return when {
            media.resourceType == null -> {
                rejected("Resource type should be specified.")
            }
            !resourceTypes.contains(media.resourceType) -> {
                rejected("${media.resourceType} is not a valid resource type.")
            }
            else -> processed()
        }
    }
}