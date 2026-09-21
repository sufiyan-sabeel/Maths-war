package com.example.data

import com.example.model.Achievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val dao: PlayerDao) {

    val playerProfileFlow: Flow<PlayerProfileEntity> = dao.getProfileFlow().map {
        it ?: PlayerProfileEntity()
    }

    suspend fun getProfile(): PlayerProfileEntity {
        return dao.getProfile() ?: PlayerProfileEntity()
    }

    suspend fun saveProfile(profile: PlayerProfileEntity) {
        dao.saveProfile(profile)
    }

    val achievementsFlow: Flow<List<Achievement>> = dao.getAllAchievementsFlow().map { records ->
        val recordMap = records.associateBy { it.id }
        Achievement.DEFAULT_ACHIEVEMENTS.map { defaultAch ->
            val record = recordMap[defaultAch.id]
            if (record != null) {
                defaultAch.copy(
                    current = record.current,
                    unlocked = record.unlocked
                )
            } else {
                defaultAch
            }
        }
    }

    suspend fun updateAchievementProgress(id: String, increment: Int) {
        val currentAchievements = Achievement.DEFAULT_ACHIEVEMENTS.associateBy { it.id }
        val ach = currentAchievements[id] ?: return
        val existing = dao.getAllAchievementsFlow()
        // Simple update
        val target = ach.target
        val newCount = minOf(target, increment)
        val isUnlocked = newCount >= target
        dao.saveAchievement(AchievementRecord(id, newCount, target, isUnlocked))
    }

    suspend fun recordCombatResult(
        scoreGained: Long,
        maxComboAchieved: Int,
        answeredCount: Int,
        correctCount: Int,
        waveReached: Int,
        xpGained: Int
    ) {
        val current = getProfile()
        val newXp = current.xp + xpGained
        val newLevel = 1 + (newXp / 500)
        val newHighScore = maxOf(current.highScore, current.highScore + scoreGained)
        val newBestCombo = maxOf(current.bestCombo, maxComboAchieved)
        val newTotalAnswered = current.totalAnswered + answeredCount
        val newTotalCorrect = current.totalCorrect + correctCount
        val newHighestWave = maxOf(current.highestWave, waveReached)

        val updated = current.copy(
            level = newLevel,
            xp = newXp,
            highScore = newHighScore,
            bestCombo = newBestCombo,
            totalAnswered = newTotalAnswered,
            totalCorrect = newTotalCorrect,
            highestWave = newHighestWave
        )
        saveProfile(updated)
    }

    val allLevelsFlow: Flow<List<LevelRecord>> = dao.getAllLevelsFlow().map { records ->
        if (records.isEmpty()) {
            (1..50).map { lvl ->
                LevelRecord(levelNumber = lvl, isUnlocked = lvl == 1)
            }
        } else {
            val map = records.associateBy { it.levelNumber }
            (1..50).map { lvl ->
                map[lvl] ?: LevelRecord(levelNumber = lvl, isUnlocked = lvl == 1)
            }
        }
    }

    suspend fun recordLevelCompleted(levelNum: Int, stars: Int, score: Long, timeSec: Float) {
        val existing = dao.getLevel(levelNum)
        val updated = LevelRecord(
            levelNumber = levelNum,
            stars = maxOf(existing?.stars ?: 0, stars),
            highScore = maxOf(existing?.highScore ?: 0L, score),
            bestTimeSec = if (existing != null && existing.bestTimeSec > 0f) minOf(existing.bestTimeSec, timeSec) else timeSec,
            isUnlocked = true,
            isCompleted = true
        )
        dao.saveLevel(updated)
        // Unlock next level if exists
        if (levelNum < 50) {
            val next = dao.getLevel(levelNum + 1)
            if (next == null || !next.isUnlocked) {
                dao.saveLevel(LevelRecord(levelNumber = levelNum + 1, isUnlocked = true))
            }
        }
    }
}
