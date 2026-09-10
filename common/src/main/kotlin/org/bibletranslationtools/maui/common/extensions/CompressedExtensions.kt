package org.bibletranslationtools.maui.common.extensions

enum class CompressedExtensions(vararg val ext: String) {
    MP3("mp3"),
    JPG("jpg", "jpeg");

    companion object : SupportedExtensions {
        override fun isSupported(ext: String) =
            values().any {
                it.name == ext.uppercase() || it.ext.contains(ext)
            }
    }
}
