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
}
