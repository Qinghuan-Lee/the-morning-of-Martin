package com.martin.morning.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.martin.morning.data.AlarmEntity
import com.martin.morning.data.RepeatType
import java.util.Calendar

/**
 * 负责向系统注册/取消精确闹钟
 */
object AlarmScheduler {

    fun schedule(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarm.id)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = nextTriggerTime(alarm)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAt, pending
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAt, pending
                )
            }
        } catch (e: SecurityException) {
            // 无精确闹钟权限，降级为非精确
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAt, pending
            )
        }
    }

    fun cancel(context: Context, alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }

    private fun nextTriggerTime(alarm: AlarmEntity): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        when (alarm.repeatType) {
            RepeatType.ONCE -> {
                if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
            }
            RepeatType.DAILY -> {
                if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
            }
            RepeatType.WEEKDAYS -> {
                while (target.before(now) || !isWeekday(target)) {
                    target.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            RepeatType.CUSTOM -> {
                val days = alarm.customDays.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
                if (days.isEmpty()) {
                    if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
                } else {
                    while (target.before(now) || target.get(Calendar.DAY_OF_WEEK) !in days.map { javaDayToAndroid(it) }) {
                        target.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
            }
        }
        return target.timeInMillis
    }

    private fun isWeekday(cal: Calendar): Boolean {
        val day = cal.get(Calendar.DAY_OF_WEEK)
        return day in Calendar.MONDAY..Calendar.FRIDAY
    }

    /** 将 1=周一...7=周日 转为 Calendar 常量 */
    private fun javaDayToAndroid(day: Int): Int {
        return when (day) {
            1 -> Calendar.MONDAY
            2 -> Calendar.TUESDAY
            3 -> Calendar.WEDNESDAY
            4 -> Calendar.THURSDAY
            5 -> Calendar.FRIDAY
            6 -> Calendar.SATURDAY
            7 -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }
}
