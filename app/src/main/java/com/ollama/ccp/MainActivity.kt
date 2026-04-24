package com.ollama.ccp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.ollama.ccp.chat.ChatScreen
import com.ollama.ccp.chat.ChatViewModel
import com.ollama.ccp.core.LLMEngine
import com.ollama.ccp.models.ModelScreen
import java.io.File

class MainActivity : ComponentActivity() {
    private val llmEngine = LLMEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel = ChatViewModel(llmEngine)

        setContent {
            var currentScreen by remember { mutableStateOf("chat") }

            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        "chat" -> ChatScreen(chatViewModel)
                        "models" -> ModelScreen(emptyList()) { /* Download */ }
                    }
                }
            }
        }
    }
}
