package com.ollama.ccp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ollama.ccp.chat.ChatScreen
import com.ollama.ccp.chat.ChatViewModel
import com.ollama.ccp.core.*
import com.ollama.ccp.models.ModelScreen
import com.ollama.ccp.settings.SettingsRepository
import com.ollama.ccp.settings.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val llmEngine = LLMEngine()
    private val localServer = LocalServer(llmEngine)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel = ChatViewModel(llmEngine)
        val settingsRepository = SettingsRepository(this)
        val systemMonitor = SystemMonitor(this)

        setContent {
            var currentScreen by remember { mutableStateOf("chat") }
            val contextSize by settingsRepository.contextSize.collectAsState(initial = 2048)
            val threadCount by settingsRepository.threadCount.collectAsState(initial = 4)
            val scope = rememberCoroutineScope()
            val memStats = remember { mutableStateOf(systemMonitor.getMemoryInfo()) }

            MaterialTheme {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentScreen == "chat",
                                onClick = { currentScreen = "chat" },
                                icon = { Text("Chat") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "models",
                                onClick = { currentScreen = "models" },
                                icon = { Text("Models") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "settings",
                                onClick = { currentScreen = "settings" },
                                icon = { Text("Settings") }
                            )
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            "chat" -> ChatScreen(chatViewModel)
                            "models" -> ModelScreen(emptyList()) { /* Download */ }
                            "settings" -> SettingsScreen(
                                contextSize = contextSize,
                                onContextSizeChange = { scope.launch { settingsRepository.updateContextSize(it) } },
                                threadCount = threadCount,
                                onThreadCountChange = { scope.launch { settingsRepository.updateThreadCount(it) } }
                            )
                        }

                        // Mini Monitor at top
                        Text(
                            "RAM: ${memStats.value.availableMemory / 1024 / 1024} MB free",
                            modifier = Modifier.padding(4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        localServer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        localServer.stop()
        llmEngine.unloadModel()
    }
}
