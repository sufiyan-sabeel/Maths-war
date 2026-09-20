package com.example.data.firebase

data class UserProfile(
    val uid: String = "",
    val username: String = "MathBrawler",
    val displayName: String = "Stickman Fighter",
    val photoUrl: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val rank: Int = 500,
    val highestRank: Int = 500,
    val totalScore: Long = 0L,
    val bestScore: Long = 0L,
    val gamesPlayed: Int = 0,
    val completedLevels: Int = 0,
    val stars: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "username" to username,
        "displayName" to displayName,
        "photoUrl" to photoUrl,
        "level" to level,
        "xp" to xp,
        "rank" to rank,
        "highestRank" to highestRank,
        "totalScore" to totalScore,
        "bestScore" to bestScore,
        "gamesPlayed" to gamesPlayed,
        "completedLevels" to completedLevels,
        "stars" to stars,
        "createdAt" to createdAt
    )

    companion object {
        fun fromMap(data: Map<String, Any?>): UserProfile {
            return UserProfile(
                uid = data["uid"] as? String ?: "",
                username = data["username"] as? String ?: "MathsWarrior",
                displayName = data["displayName"] as? String ?: "Stickman Fighter",
                photoUrl = data["photoUrl"] as? String ?: "",
                level = (data["level"] as? Number)?.toInt() ?: 1,
                xp = (data["xp"] as? Number)?.toInt() ?: 0,
                rank = (data["rank"] as? Number)?.toInt() ?: 500,
                highestRank = (data["highestRank"] as? Number)?.toInt() ?: 500,
                totalScore = (data["totalScore"] as? Number)?.toLong() ?: 0L,
                bestScore = (data["bestScore"] as? Number)?.toLong() ?: 0L,
                gamesPlayed = (data["gamesPlayed"] as? Number)?.toInt() ?: 0,
                completedLevels = (data["completedLevels"] as? Number)?.toInt() ?: 0,
                stars = (data["stars"] as? Number)?.toInt() ?: 0,
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}

data class LeaderboardEntry(
    val rank: Int,
    val uid: String,
    val username: String,
    val displayName: String,
    val level: Int,
    val score: Long,
    val xp: Int,
    val completedLevels: Int,
    val isCurrentUser: Boolean = false
)

enum class RankTier(val title: String, val minRank: Int, val maxRank: Int, val colorHex: Long) {
    CELESTIAL("CELESTIAL MATH", 1, 10, 0xFFE67E22),
    GRANDMASTER("GRANDMASTER", 11, 49, 0xFFD35400),
    MASTER("MASTER", 50, 149, 0xFFC0392B),
    DIAMOND("DIAMOND", 150, 299, 0xFF2980B9),
    PLATINUM("PLATINUM", 300, 499, 0xFF16A085),
    GOLD("GOLD", 500, 749, 0xFFF39C12),
    SILVER("SILVER", 750, 999, 0xFF95A5A6),
    BRONZE("BRONZE", 1000, Int.MAX_VALUE, 0xFF7F8C8D);

    companion object {
        fun fromRank(rank: Int): RankTier {
            return entries.find { rank in it.minRank..it.maxRank } ?: BRONZE
        }
    }
}
