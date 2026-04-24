package com.ollama.ccp.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contextSize: Int,
    onContextSizeChange: (Int) -> Unit,
    threadCount: Int,
    onThreadCountChange: (Int) -> Unit,
    temp: Float,
    onTempChange: (Float) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Inference Settings", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Context Size: $contextSize")
            Slider(
                value = contextSize.toFloat(),
                onValueChange = { onContextSizeChange(it.toInt()) },
                valueRange = 512f..8192f,
                steps = 15
            )

            Text("Threads: $threadCount")
            Slider(
                value = threadCount.toFloat(),
                onValueChange = { onThreadCountChange(it.toInt()) },
                valueRange = 1f..16f,
                steps = 15
            )

            Text("Temperature: $temp")
            Slider(
                value = temp,
                onValueChange = { onTempChange(it) },
                valueRange = 0f..2f,
                steps = 20
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Server Settings", style = MaterialTheme.typography.titleMedium)
            Text("Port: 11434 (Fixed for now)")
        }
    }
}
