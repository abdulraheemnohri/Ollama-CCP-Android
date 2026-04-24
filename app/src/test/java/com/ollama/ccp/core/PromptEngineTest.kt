package com.ollama.ccp.core

import org.junit.Assert.assertEquals
import org.junit.Test

class PromptEngineTest {
    @Test
    fun testFormatPrompt() {
        val engine = PromptEngine()
        val result = engine.formatPrompt("System", "User")
        val expected = """
            <|system|>
            System
            <|user|>
            User
            <|assistant|>
        """.trimIndent()
        assertEquals(expected, result)
    }
}
