package com.ollama.ccp.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelScreen(models: List<File>, onDownloadClick: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Model Manager") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onDownloadClick) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(models) { model ->
                ListItem(
                    headlineContent = { Text(model.name) },
                    supportingContent = { Text("${model.length() / 1024 / 1024} MB") }
                )
            }
        }
    }
}
