package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val xp: Int = 0,
    val highScore: Long = 0L,
    val bestCombo: Int = 0,
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val highestWave: Int = 1,
    val completedWorlds: Int = 1,
    val stars: Int = 0,
    val selectedSkinId: String = "classic",
    val unlockedSkinIds: String = "classic,cyan_cyber",
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val explanationsEnabled: Boolean = true
)

@Entity(tableName = "achievements")
data class AchievementRecord(
    @PrimaryKey val id: String,
    val current: Int,
    val target: Int,
    val unlocked: Boolean
)

@Entity(tableName = "levels")
data class LevelRecord(
    @PrimaryKey val levelNumber: Int,
    val stars: Int = 0,
    val highScore: Long = 0L,
    val bestTimeSec: Float = 0f,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false
)

