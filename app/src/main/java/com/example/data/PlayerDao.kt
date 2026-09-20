package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM achievements")
    fun getAllAchievementsFlow(): Flow<List<AchievementRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAchievements(records: List<AchievementRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAchievement(record: AchievementRecord)
}
