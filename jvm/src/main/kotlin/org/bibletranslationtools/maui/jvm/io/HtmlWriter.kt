package org.bibletranslationtools.maui.jvm.io

import java.io.File

class HtmlWriter {
    fun write(filename:String, content:String) {
        val file = File(filename)
        file.printWriter().use {
            it.println(content)
        }
    }
}