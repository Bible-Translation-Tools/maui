package org.bibletranslationtools.maui.common.io

interface IResourceReader {
    fun read(): List<String>
}

interface IBooksReader : IResourceReader
interface ILanguagesReader : IResourceReader
interface IResourceTypesReader : IResourceReader
