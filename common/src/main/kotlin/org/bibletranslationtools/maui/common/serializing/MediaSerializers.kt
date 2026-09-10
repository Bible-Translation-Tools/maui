package org.bibletranslationtools.maui.common.serializing

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.bibletranslationtools.maui.common.data.Media

class MediaSerializer : JsonSerializer<Lazy<List<Media>>>() {
    override fun serialize(lazyList: Lazy<List<Media>>, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartArray()
        lazyList.value.forEach { media ->
            gen.writeObject(media)
        }
        gen.writeEndArray()
    }
}

class MediaDeserializer : JsonDeserializer<Lazy<List<Media>>>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): Lazy<List<Media>> {
        val reference = object : TypeReference<List<Media>>() {}
        val list = parser.readValueAs<List<Media>>(reference)

        return lazy {
            list
        }
    }
}