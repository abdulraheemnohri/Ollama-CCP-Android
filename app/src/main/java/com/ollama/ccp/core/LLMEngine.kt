package com.ollama.ccp.core

import android.util.Log

class LLMEngine {
    companion object {
        init {
            System.loadLibrary("ollamaccp")
        }
    }

    external fun loadModel(modelPath: String): Boolean
    external fun unloadModel()
    external fun generate(prompt: String): String

    fun chat(prompt: String): String {
        return generate(prompt)
    }
}
