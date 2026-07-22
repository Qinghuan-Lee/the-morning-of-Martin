package com.martin.morning.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * 贪睡调度 - 10 分钟后重新触发闹钟
 */
object SnoozeScheduler {

    private const val SNOOZE_DURATION_MS = 10 * 60 * 1000L // 10 分钟

    fun schedule(context: Context, alarmId: Long, snoozeCount: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("is_snooze", true)
            putExtra("snooze_count", snoozeCount)
        }
        // 用 alarmId + 100000 作为 snooze 的 requestCode，避免与正常闹钟冲突
        val pending = PendingIntent.getBroadcast(
            context,
            (alarmId + 100000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = System.currentTimeMillis() + SNOOZE_DURATION_MS
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, triggerAt, pending
        )
    }

    fun cancel(context: Context, alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            (alarmId + 100000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }
}
