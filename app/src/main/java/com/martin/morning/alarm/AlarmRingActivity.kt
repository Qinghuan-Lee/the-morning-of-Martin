package com.martin.morning.alarm

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.martin.morning.MartinMorningApp
import com.martin.morning.R
import com.martin.morning.data.AlarmLogEntity
import com.martin.morning.player.PlaybackService
import kotlinx.coroutines.launch

/**
 * 全屏响铃页面 - 锁屏上显示
 */
class AlarmRingActivity : AppCompatActivity() {

    private var alarmId: Long = -1
    private var currentSongId: Long? = null
    private var snoozeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 锁屏上显示 + 亮屏
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_alarm_ring)

        alarmId = intent.getLongExtra("alarm_id", -1)
        if (alarmId == -1L) { finish(); return }

        val tvSong = findViewById<TextView>(R.id.tv_song_name)
        val tvCategory = findViewById<TextView>(R.id.tv_category)
        val btnDismiss = findViewById<Button>(R.id.btn_dismiss)
        val btnSnooze = findViewById<Button>(R.id.btn_snooze)

        // 抽歌并播放
        lifecycleScope.launch {
            val db = MartinMorningApp.instance.database
            val alarm = db.alarmDao().getById(alarmId) ?: run { finish(); return@launch }
            val category = db.categoryDao().getById(alarm.categoryId)
            val song = db.songDao().getRandomFromCategory(alarm.categoryId)

            tvCategory.text = category?.name ?: "未分类"

            if (song != null) {
                currentSongId = song.id
                tvSong.text = song.title
                PlaybackService.startPlayback(this@AlarmRingActivity, song.filePath)

                // 记录响铃日志
                db.alarmLogDao().insert(
                    AlarmLogEntity(
                        alarmId = alarmId,
                        songId = song.id,
                        triggeredAt = System.currentTimeMillis()
                    )
                )
            } else {
                tvSong.text = "（该分类无歌曲）"
            }
        }

        btnDismiss.setOnClickListener {
            PlaybackService.stopPlayback(this)
            recordDismiss()
            finish()
        }

        btnSnooze.setOnClickListener {
            PlaybackService.stopPlayback(this)
            snoozeCount++
            SnoozeScheduler.schedule(this, alarmId, snoozeCount)
            recordSnooze()
            finish()
        }
    }

    private fun recordDismiss() {
        lifecycleScope.launch {
            val db = MartinMorningApp.instance.database
            val logs = db.alarmLogDao().getRecent(1)
            // 更新最近一条日志的关闭时间
        }
    }

    private fun recordSnooze() {
        lifecycleScope.launch {
            // 记录贪睡
        }
    }
}
