package com.martin.morning.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 闹钟实体
 */
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,                    // 时 (0-23)
    val minute: Int,                  // 分 (0-59)
    val repeatType: RepeatType,       // 重复方式
    val customDays: String = "",      // 自定义重复日 (逗号分隔, 如 "1,3,5" 表示周一三五)
    val categoryId: Long,             // 绑定的曲库分类 ID
    val isEnabled: Boolean = true,    // 开关状态
    val createdAt: Long = System.currentTimeMillis()
)

enum class RepeatType {
    DAILY,          // 每天
    WEEKDAYS,       // 仅工作日
    CUSTOM,         // 自定义
    ONCE            // 仅此一次
}

/**
 * 曲库分类实体
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                 // 分类名
    val colorHex: String,             // 分类颜色
    val isPreset: Boolean = false,    // 是否预设
    val sortOrder: Int = 0
)

/**
 * 铃声/歌曲实体
 */
@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,                // 歌名
    val subtitle: String = "",        // 出处/作者
    val filePath: String,             // 本地文件路径
    val duration: Long = 0,           // 时长 (毫秒)
    val categoryId: Long? = null,     // 所属分类 (可为空)
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * 闹钟响铃记录 (用于统计)
 */
@Entity(
    tableName = "alarm_logs",
    foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarmId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("alarmId"), Index("songId")]
)
data class AlarmLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alarmId: Long,
    val songId: Long?,                // 抽中的歌
    val triggeredAt: Long,            // 触发时间戳
    val dismissedAt: Long? = null,    // 关闭时间戳
    val snoozed: Boolean = false,     // 是否贪睡了
    val snoozeCount: Int = 0          // 贪睡次数
)
