package com.ollama.ccp.core

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.content.Intent
import android.content.IntentFilter

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

    fun getBatteryLevel(): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }

    data class MemoryStats(
        val availableMemory: Long,
        val totalMemory: Long,
        val threshold: Long,
        val lowMemory: Boolean
    )
}
