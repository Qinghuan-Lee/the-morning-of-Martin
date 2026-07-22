package com.martin.morning.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    fun getAll(): LiveData<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1")
    suspend fun getEnabled(): List<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getById(id: Long): AlarmEntity?

    @Insert
    suspend fun insert(alarm: AlarmEntity): Long

    @Update
    suspend fun update(alarm: AlarmEntity)

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder, name")
    fun getAll(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortOrder, name")
    suspend fun getAllSync(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)
}

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY addedAt DESC")
    fun getAll(): LiveData<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE categoryId = :categoryId ORDER BY title")
    fun getByCategory(categoryId: Long): LiveData<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE categoryId = :categoryId ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomFromCategory(categoryId: Long): SongEntity?

    @Query("SELECT * FROM songs WHERE categoryId = :categoryId AND id != :excludeId ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomFromCategoryExcluding(categoryId: Long, excludeId: Long): SongEntity?

    @Query("SELECT COUNT(*) FROM songs WHERE categoryId = :categoryId")
    suspend fun countByCategory(categoryId: Long): Int

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun count(): Int

    @Insert
    suspend fun insert(song: SongEntity): Long

    @Update
    suspend fun update(song: SongEntity)

    @Delete
    suspend fun delete(song: SongEntity)

    @Query("UPDATE songs SET categoryId = :categoryId WHERE id = :songId")
    suspend fun setCategory(songId: Long, categoryId: Long?)

    @Query("UPDATE songs SET categoryId = NULL WHERE id = :songId")
    suspend fun clearCategory(songId: Long)
}

@Dao
interface AlarmLogDao {
    @Query("SELECT * FROM alarm_logs ORDER BY triggeredAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 50): LiveData<List<AlarmLogEntity>>

    @Query("SELECT * FROM alarm_logs WHERE triggeredAt BETWEEN :from AND :to ORDER BY triggeredAt")
    suspend fun getBetween(from: Long, to: Long): List<AlarmLogEntity>

    @Query("SELECT songId, COUNT(*) as cnt FROM alarm_logs WHERE songId IS NOT NULL GROUP BY songId ORDER BY cnt DESC LIMIT :limit")
    suspend fun getTopSongs(limit: Int = 10): List<SongPlayCount>

    @Query("SELECT COUNT(*) FROM alarm_logs WHERE snoozed = 1 AND triggeredAt BETWEEN :from AND :to")
    suspend fun snoozeCountBetween(from: Long, to: Long): Int

    @Query("SELECT COUNT(DISTINCT date(triggeredAt / 1000, 'unixepoch')) FROM alarm_logs WHERE dismissedAt IS NOT NULL AND triggeredAt BETWEEN :from AND :to")
    suspend fun activeDaysBetween(from: Long, to: Long): Int

    @Insert
    suspend fun insert(log: AlarmLogEntity): Long

    @Update
    suspend fun update(log: AlarmLogEntity)
}

data class SongPlayCount(
    val songId: Long,
    val cnt: Int
)
