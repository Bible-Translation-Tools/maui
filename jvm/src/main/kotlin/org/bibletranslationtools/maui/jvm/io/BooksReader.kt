package org.bibletranslationtools.maui.jvm.io

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bibletranslationtools.maui.common.io.IBooksReader
import javax.inject.Inject

class BooksReader @Inject constructor() : IBooksReader {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class BookSchema(
        val slug: String
    )

    override fun read(): List<String> {
        return parseBooks()
    }

    private fun parseBooks(): List<String> {
        val booksFile = javaClass.getResource("/book_catalog.json")?.openStream()

        booksFile?.use { inputStream ->
            val booksList: List<BookSchema> = jacksonObjectMapper().readValue(inputStream)

            return booksList.map {
                it.slug
            }
        } ?: return listOf()
    }
}
