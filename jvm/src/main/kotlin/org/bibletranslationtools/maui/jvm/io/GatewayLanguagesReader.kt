package org.bibletranslationtools.maui.jvm.io

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.bibletranslationtools.maui.common.io.ILanguagesReader
import javax.inject.Inject

class GatewayLanguagesReader @Inject constructor() : ILanguagesReader {

    companion object {
        const val PORT_LANGUAGE_CODE_ID = "IETF Tag"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class PortGatewayLanguage(
        @JsonProperty(PORT_LANGUAGE_CODE_ID)
        val code: String
    )

    override fun read(): List<String> {
        return parseLanguages()
    }

    private fun parseLanguages(): List<String> {
        val languagesFile = javaClass.getResource("/port_gateway_languages.csv")?.openStream()

        val mapper = CsvMapper()
        val schema = CsvSchema.emptySchema().withHeader()

        languagesFile?.use { inputStream ->
            val languagesIterator: MappingIterator<PortGatewayLanguage> = mapper.readerFor(
                PortGatewayLanguage::class.java
            )
                .with(schema)
                .readValues(inputStream)

            val languages = mutableListOf<String>()
            languagesIterator.forEach {
                languages.add(it.code)
            }

            languages.sort()

            return languages
        } ?: return listOf()
    }
}
