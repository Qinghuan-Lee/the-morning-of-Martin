package com.martin.morning.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.martin.morning.MartinMorningApp
import com.martin.morning.data.RepeatType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", -1)
        if (alarmId == -1L) return

        // 启动响铃 Activity
        val ringIntent = Intent(context, AlarmRingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra("alarm_id", alarmId)
        }
        context.startActivity(ringIntent)

        // 如果是仅此一次，触发后自动关闭
        CoroutineScope(Dispatchers.IO).launch {
            val db = MartinMorningApp.instance.database
            val alarm = db.alarmDao().getById(alarmId) ?: return@launch
            if (alarm.repeatType == RepeatType.ONCE) {
                db.alarmDao().setEnabled(alarmId, false)
                AlarmScheduler.cancel(context, alarmId)
            } else {
                // 重复闹钟：注册下一次
                AlarmScheduler.schedule(context, alarm)
            }
        }
    }
}
