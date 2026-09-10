package org.bibletranslationtools.maui.jvm.io

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bibletranslationtools.maui.common.io.ILanguagesReader
import javax.inject.Inject

class AllLanguagesReader @Inject constructor() : ILanguagesReader {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class LanguageSchema(
        val lc: String
    )

    override fun read(): List<String> {
        return parseLanguages()
    }

    private fun parseLanguages(): List<String> {
        val languagesFile = javaClass.getResource("/langnames.json")?.openStream()

        languagesFile?.use { inputStream ->
            val languagesList: List<LanguageSchema> = jacksonObjectMapper().readValue(inputStream)

            return languagesList.map {
                it.lc
            }
        } ?: return listOf()
    }
}