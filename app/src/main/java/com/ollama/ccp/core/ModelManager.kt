package com.ollama.ccp.core

import android.content.Context
import java.io.File

class ModelManager(private val context: Context) {
    fun getModelsDir(): File {
        val dir = File(context.getExternalFilesDir(null), "models")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun listModels(): List<File> {
        return getModelsDir().listFiles { file -> file.extension == "gguf" }?.toList() ?: emptyList()
    }

    fun deleteModel(file: File): Boolean {
        return file.delete()
    }
}
