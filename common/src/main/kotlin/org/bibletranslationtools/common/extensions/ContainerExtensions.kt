package org.bibletranslationtools.common.extensions

enum class ContainerExtensions(val ext: String) {
    TR("tr");

    companion object: SupportedExtensions {
        override fun isSupported(ext: String) =
            values().any {
                it.name == ext.toUpperCase() || it.ext == ext
            }
    }
}
