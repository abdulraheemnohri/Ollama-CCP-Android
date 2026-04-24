package com.ollama.ccp.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ollama.ccp.core.HFDownloader
import java.io.File
import okhttp3.OkHttpClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelScreen(models: List<File>, modelsDir: File, onDelete: (File) -> Unit) {
    var showDownloadDialog by remember { mutableStateOf(false) }
    var repo by remember { mutableStateOf("TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF") }
    var filename by remember { mutableStateOf("tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf") }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }

    val downloader = remember { HFDownloader(OkHttpClient()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Model Manager") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDownloadDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isDownloading) {
                LinearProgressIndicator(
                    progress = { downloadProgress },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(models) { model ->
                    ListItem(
                        headlineContent = { Text(model.name) },
                        supportingContent = { Text("${model.length() / 1024 / 1024} MB") },
                        trailingContent = {
                            IconButton(onClick = { onDelete(model) }) {
                                Text("Del")
                            }
                        }
                    )
                }
            }
        }

        if (showDownloadDialog) {
            AlertDialog(
                onDismissRequest = { showDownloadDialog = false },
                title = { Text("Download GGUF from HF") },
                text = {
                    Column {
                        TextField(value = repo, onValueChange = { repo = it }, label = { Text("Repo (user/name)") })
                        TextField(value = filename, onValueChange = { filename = it }, label = { Text("Filename (.gguf)") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showDownloadDialog = false
                        isDownloading = true
                        val destFile = File(modelsDir, filename)
                        downloader.downloadModel(repo, filename, destFile, object : HFDownloader.DownloadListener {
                            override fun onProgress(progress: Float, bytesRead: Long, totalBytes: Long) {
                                downloadProgress = progress
                            }
                            override fun onComplete(file: File) {
                                isDownloading = false
                            }
                            override fun onError(e: Exception) {
                                isDownloading = false
                            }
                        })
                    }) {
                        Text("Download")
                    }
                }
            )
        }
    }
}
