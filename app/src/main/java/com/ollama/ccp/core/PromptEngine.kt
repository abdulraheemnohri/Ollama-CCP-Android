package com.ollama.ccp.core

class PromptEngine {
    fun formatPrompt(systemPrompt: String, userMessage: String): String {
        return """
            <|system|>
            $systemPrompt
            <|user|>
            $userMessage
            <|assistant|>
        """.trimIndent()
    }
}
