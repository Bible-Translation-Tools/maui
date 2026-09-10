package org.bibletranslationtools.maui.jvm.io

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bibletranslationtools.maui.common.io.IResourceTypesReader
import javax.inject.Inject


class ResourceTypesReader @Inject constructor() : IResourceTypesReader {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ResourceTypeSchema(
        val slug: String
    )

    override fun read(): List<String> {
        return parseResourceTypes()
    }

    private fun parseResourceTypes(): List<String> {
        val resourceTypesFile = javaClass.getResource("/resource_types.json")?.openStream()

        resourceTypesFile?.use { inputStream ->
            val resourceTypes: List<ResourceTypeSchema> = jacksonObjectMapper().readValue(inputStream)

            return resourceTypes.map {
                it.slug
            }
        } ?: return listOf()
    }
}