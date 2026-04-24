package com.ollama.ccp.core

import android.app.ActivityManager
import android.content.Context
import android.os.Debug

class SystemMonitor(private val context: Context) {
    fun getMemoryInfo(): MemoryStats {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        return MemoryStats(
            availableMemory = memoryInfo.availMem,
            totalMemory = memoryInfo.totalMem,
            threshold = memoryInfo.threshold,
            lowMemory = memoryInfo.lowMemory
        )
    }

    data class MemoryStats(
        val availableMemory: Long,
        val totalMemory: Long,
        val threshold: Long,
        val lowMemory: Boolean
    )
}
