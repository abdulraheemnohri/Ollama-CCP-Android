package com.ollama.ccp.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        val CONTEXT_SIZE = intPreferencesKey("context_size")
        val THREAD_COUNT = intPreferencesKey("thread_count")
        val SERVER_PORT = intPreferencesKey("server_port")
        val SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
        val TEMPERATURE = floatPreferencesKey("temperature")
    }

    val contextSize: Flow<Int> = context.dataStore.data.map { it[CONTEXT_SIZE] ?: 2048 }
    val threadCount: Flow<Int> = context.dataStore.data.map { it[THREAD_COUNT] ?: 4 }
    val serverPort: Flow<Int> = context.dataStore.data.map { it[SERVER_PORT] ?: 11434 }
    val systemPrompt: Flow<String> = context.dataStore.data.map { it[SYSTEM_PROMPT] ?: "You are a helpful assistant." }
    val temperature: Flow<Float> = context.dataStore.data.map { it[TEMPERATURE] ?: 0.7f }

    suspend fun updateContextSize(size: Int) {
        context.dataStore.edit { it[CONTEXT_SIZE] = size }
    }

    suspend fun updateThreadCount(count: Int) {
        context.dataStore.edit { it[THREAD_COUNT] = count }
    }

    suspend fun updateTemperature(temp: Float) {
        context.dataStore.edit { it[TEMPERATURE] = temp }
    }
}
