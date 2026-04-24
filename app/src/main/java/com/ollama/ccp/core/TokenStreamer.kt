package com.ollama.ccp.core

interface TokenCallback {
    fun onToken(token: String)
    fun onComplete()
}

class TokenStreamer(private val callback: TokenCallback) {
    // This will be called from JNI
    fun onNativeToken(token: String) {
        callback.onToken(token)
    }
}
