package com.ollama.ccp.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ollama.ccp.core.LLMEngine
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

        viewModelScope.launch {
            _isGenerating.value = true
            val response = llmEngine.chat(content)
            val assistantMsg = Message("assistant", response)
            _messages.value = _messages.value + assistantMsg
            _isGenerating.value = false
        }
    }
}
