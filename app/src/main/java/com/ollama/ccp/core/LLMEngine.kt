package com.ollama.ccp.core

import android.util.Log

class LLMEngine {
    companion object {
        init {
            System.loadLibrary("ollamaccp")
        }
    }

    external fun loadModel(modelPath: String, n_ctx: Int, n_threads: Int): Boolean
    external fun unloadModel()
    external fun generateStreaming(prompt: String, callback: TokenCallback)

    fun chatStream(prompt: String, callback: TokenCallback) {
        generateStreaming(prompt, callback)
    }
}
