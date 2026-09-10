package org.bibletranslationtools.maui.jvm.io

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bibletranslationtools.maui.common.io.IVersificationReader
import org.bibletranslationtools.maui.common.io.Versification
import javax.inject.Inject

class VersificationReader @Inject constructor() : IVersificationReader {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private class VersificationSchema(
        val maxVerses: Map<String, List<Int>>
    )

    override fun read() = parseVersification()

    private fun parseVersification(): Versification {
        val versificationFile = javaClass.getResource("/eng.json").openStream()

        versificationFile.use { inputStream ->
            val versification: VersificationSchema = jacksonObjectMapper().readValue(inputStream)
            return versification.maxVerses
        }
    }
}