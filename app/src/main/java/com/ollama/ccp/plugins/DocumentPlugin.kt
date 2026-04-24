package com.ollama.ccp.plugins

import java.io.File

class DocumentPlugin : Plugin {
    override val name = "Document Search"

    override fun execute(input: String): String {
        // Mock RAG: searching for "input" in documents
        return "Searching for '$input' in local documents... Found 2 matches."
    }

    fun readTxt(file: File): String {
        return file.readText()
    }
}
