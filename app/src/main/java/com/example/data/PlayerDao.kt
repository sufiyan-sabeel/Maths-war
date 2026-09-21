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

    @Query("SELECT * FROM levels ORDER BY levelNumber ASC")
    fun getAllLevelsFlow(): Flow<List<LevelRecord>>

    @Query("SELECT * FROM levels WHERE levelNumber = :lvl LIMIT 1")
    suspend fun getLevel(lvl: Int): LevelRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLevel(level: LevelRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLevels(levels: List<LevelRecord>)
}
