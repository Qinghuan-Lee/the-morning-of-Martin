package com.martin.morning.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.martin.morning.MartinMorningApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 开机后重新注册所有已启用的闹钟
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = MartinMorningApp.instance.database
                val alarms = db.alarmDao().getEnabled()
                alarms.forEach { alarm ->
                    AlarmScheduler.schedule(context, alarm)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
