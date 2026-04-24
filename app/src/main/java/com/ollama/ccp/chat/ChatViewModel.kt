package com.ollama.ccp.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ollama.ccp.core.LLMEngine
import com.ollama.ccp.core.TokenCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val llmEngine: LLMEngine) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun sendMessage(content: String) {
        val userMsg = Message("user", content)
        _messages.value = _messages.value + userMsg

        val assistantMsgIndex = _messages.value.size
        _messages.value = _messages.value + Message("assistant", "")

        _isGenerating.value = true

        viewModelScope.launch {
            llmEngine.chatStream(content, object : TokenCallback {
                override fun onToken(token: String) {
                    val currentMessages = _messages.value.toMutableList()
                    val msg = currentMessages[assistantMsgIndex]
                    currentMessages[assistantMsgIndex] = msg.copy(content = msg.content + token)
                    _messages.value = currentMessages
                }

                override fun onComplete() {
                    _isGenerating.value = false
                }
            })
        }
    }
}
