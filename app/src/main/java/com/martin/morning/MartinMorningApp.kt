package com.martin.morning

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.martin.morning.data.AppDatabase

class MartinMorningApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val alarmChannel = NotificationChannel(
            CHANNEL_ALARM,
            "闹钟响铃",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "闹钟到点响铃通知"
            setBypassDnd(true)
        }

        val playbackChannel = NotificationChannel(
            CHANNEL_PLAYBACK,
            "音频播放",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "铃声播放前台服务通知"
        }

        manager.createNotificationChannel(alarmChannel)
        manager.createNotificationChannel(playbackChannel)
    }

    companion object {
        const val CHANNEL_ALARM = "alarm_channel"
        const val CHANNEL_PLAYBACK = "playback_channel"

        lateinit var instance: MartinMorningApp
            private set
    }
}
