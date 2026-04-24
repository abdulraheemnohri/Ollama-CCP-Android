package com.ollama.ccp.plugins

interface Plugin {
    val name: String
    fun execute(input: String): String
}

class FilePlugin : Plugin {
    override val name = "File Reader"
    override fun execute(input: String): String = "Reading file: $input (Mock)"
}

class CodePlugin : Plugin {
    override val name = "Code Executor"
    override fun execute(input: String): String = "Executing code: $input (Mock)"
}
