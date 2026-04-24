package com.ollama.ccp.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Sessions", modifier = Modifier.padding(16.dp))
                Button(onClick = { viewModel.newSession() }, modifier = Modifier.padding(16.dp)) {
                    Text("New Chat")
                }
                LazyColumn {
                    items(sessions) { sessionId ->
                        NavigationDrawerItem(
                            label = { Text(sessionId.take(8)) },
                            selected = sessionId == currentSessionId,
                            onClick = { viewModel.selectSession(sessionId) }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Chat: ${currentSessionId.take(8)}") }) }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(messages) { msg ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(msg.role.uppercase(), style = MaterialTheme.typography.labelSmall)
                            Markdown(content = msg.content)
                        }
                    }
                }
                if (isGenerating) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") }
                    )
                    Button(onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    }) {
                        Text("Send")
                    }
                }
            }
        }
    }
}
