package org.bibletranslationtools.maui.common.validators

import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.errors.InvalidRCException
import java.io.File

class OratureValidator(private val file: File) {
    private val creatorName = "Orature"

    fun validate() {
        ResourceContainer.load(file).use {
            if (it.manifest.dublinCore.creator != creatorName) {
                throw InvalidRCException("Creator name in the manifest should be $creatorName")
            }
        }
    }
}