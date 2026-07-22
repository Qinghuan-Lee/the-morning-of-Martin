package com.martin.morning.player

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.martin.morning.MainActivity
import com.martin.morning.MartinMorningApp
import com.martin.morning.R

/**
 * 前台服务：播放闹钟铃声
 */
class PlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val path = intent.getStringExtra(EXTRA_FILE_PATH) ?: return START_NOT_STICKY
                play(path)
            }
            ACTION_STOP -> {
                stop()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun play(filePath: String) {
        stop()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            isLooping = true // 闹钟铃声循环播放直到用户关闭
            setOnPreparedListener { start() }
            setOnErrorListener { _, _, _ ->
                stopSelf()
                true
            }
            prepareAsync()
        }

        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, MartinMorningApp.CHANNEL_PLAYBACK)
            .setContentTitle("正在播放闹钟铃声")
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(pending)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    companion object {
        const val ACTION_PLAY = "com.martin.morning.PLAY"
        const val ACTION_STOP = "com.martin.morning.STOP"
        const val EXTRA_FILE_PATH = "file_path"
        private const val NOTIFICATION_ID = 1001

        fun startPlayback(context: Context, filePath: String) {
            val intent = Intent(context, PlaybackService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_FILE_PATH, filePath)
            }
            context.startForegroundService(intent)
        }

        fun stopPlayback(context: Context) {
            val intent = Intent(context, PlaybackService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
