package com.ollama.ccp.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ollama.ccp.core.LLMEngine
import com.ollama.ccp.core.TokenCallback
import com.ollama.ccp.db.ChatDao
import com.ollama.ccp.db.ChatEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(private val llmEngine: LLMEngine, private val chatDao: ChatDao) : ViewModel() {
    private val _currentSessionId = MutableStateFlow(UUID.randomUUID().toString())
    val currentSessionId = _currentSessionId.asStateFlow()

    val messages = _currentSessionId.flatMapLatest { sessionId ->
        chatDao.getMessagesForSession(sessionId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sessions = chatDao.getAllSessionIds().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val sessionId = _currentSessionId.value
            chatDao.insertMessage(ChatEntity(sessionId = sessionId, role = "user", content = content))

            _isGenerating.value = true
            var assistantContent = ""

            llmEngine.chatStream(content, object : TokenCallback {
                override fun onToken(token: String) {
                    assistantContent += token
                    // For performance, we might want to buffer or only update UI,
                    // but for Room we usually wait for complete or update periodically.
                    // Here we'll just update a local state for the UI to be snappy
                }

                override fun onComplete() {
                    viewModelScope.launch {
                        chatDao.insertMessage(ChatEntity(sessionId = sessionId, role = "assistant", content = assistantContent))
                        _isGenerating.value = false
                    }
                }
            })
        }
    }

    fun selectSession(sessionId: String) {
        _currentSessionId.value = sessionId
    }

    fun newSession() {
    fun exportSessionAsMarkdown(sessionId: String): String {
        val currentMessages = messages.value
        return currentMessages.joinToString("\n\n") {
            "**${it.role.uppercase()}**:\n${it.content}"
        }
    }
        _currentSessionId.value = UUID.randomUUID().toString()
    }
}
