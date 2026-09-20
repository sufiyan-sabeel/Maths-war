package com.example.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LeaderboardRepository {

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.w("LeaderboardRepo", "Firestore init skipped: ${e.message}")
        null
    }

    suspend fun getTop300Leaderboard(currentUserUid: String): List<LeaderboardEntry> = withContext(Dispatchers.IO) {
        if (firestore == null) {
            return@withContext generateFallbackLeaderboard(currentUserUid)
        }

        try {
            val snapshot = firestore.collection("users")
                .orderBy("totalScore", Query.Direction.DESCENDING)
                .limit(300)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return@withContext generateFallbackLeaderboard(currentUserUid)
            }

            val entries = mutableListOf<LeaderboardEntry>()
            var rank = 1
            for (doc in snapshot.documents) {
                val data = doc.data ?: continue
                val uid = doc.id
                val username = data["username"] as? String ?: "Warrior$rank"
                val displayName = data["displayName"] as? String ?: username
                val score = (data["totalScore"] as? Number)?.toLong() ?: 0L
                val xp = (data["xp"] as? Number)?.toInt() ?: 0
                val level = (data["level"] as? Number)?.toInt() ?: 1
                val completedLevels = (data["completedLevels"] as? Number)?.toInt() ?: 0

                entries.add(
                    LeaderboardEntry(
                        rank = rank,
                        uid = uid,
                        username = username,
                        displayName = displayName,
                        level = level,
                        score = score,
                        xp = xp,
                        completedLevels = completedLevels,
                        isCurrentUser = (uid == currentUserUid)
                    )
                )
                rank++
            }

            // If user is not in top 300, append user info
            val isUserInTop = entries.any { it.uid == currentUserUid }
            if (!isUserInTop && currentUserUid.isNotEmpty()) {
                val userDoc = firestore.collection("users").document(currentUserUid).get().await()
                if (userDoc.exists() && userDoc.data != null) {
                    val uData = userDoc.data!!
                    val uScore = (uData["totalScore"] as? Number)?.toLong() ?: 0L
                    val uRank = (uData["rank"] as? Number)?.toInt() ?: 342
                    entries.add(
                        LeaderboardEntry(
                            rank = uRank,
                            uid = currentUserUid,
                            username = uData["username"] as? String ?: "You",
                            displayName = uData["displayName"] as? String ?: "You",
                            level = (uData["level"] as? Number)?.toInt() ?: 1,
                            score = uScore,
                            xp = (uData["xp"] as? Number)?.toInt() ?: 0,
                            completedLevels = (uData["completedLevels"] as? Number)?.toInt() ?: 0,
                            isCurrentUser = true
                        )
                    )
                }
            }

            entries
        } catch (e: Exception) {
            Log.w("LeaderboardRepo", "Fetch top 300 failed: ${e.message}")
            generateFallbackLeaderboard(currentUserUid)
        }
    }

    private fun generateFallbackLeaderboard(currentUserUid: String): List<LeaderboardEntry> {
        val famousMathematicians = listOf(
            "EulerPrime", "GaussMaster", "NewtonStrike", "Ramanujan99", "NoetherVector",
            "FourierPulse", "RiemannHypo", "TuringCipher", "PascalTriangle", "FermatLast",
            "ArchimedesPi", "HypatiaStar", "LeibnizFlux", "GaloisField", "CantorSet",
            "HilbertSpace", "DescartesGrid", "LaplaceDemon", "FibonacciSpiral", "PythagorasRoot",
            "BabbageEngine", "LovelaceCode", "PoincareOrb", "CauchySeq", "DedekindCut",
            "Brahmagupta0", "AlKhwarizmi", "GodelLogic", "BooleBit", "VennDiagram"
        )

        val list = mutableListOf<LeaderboardEntry>()
        var score = 154200L

        for (i in 1..50) {
            val name = if (i - 1 < famousMathematicians.size) famousMathematicians[i - 1] else "MathMaster$i"
            list.add(
                LeaderboardEntry(
                    rank = i,
                    uid = "bot_$i",
                    username = name,
                    displayName = name,
                    level = 50 - (i / 2),
                    score = score,
                    xp = (score / 4).toInt(),
                    completedLevels = 50 - (i / 3),
                    isCurrentUser = false
                )
            )
            score = (score * 0.96).toLong()
        }

        // Add current user entry at rank ~391
        list.add(
            LeaderboardEntry(
                rank = 391,
                uid = currentUserUid.ifEmpty { "current_user" },
                username = "MathsWarrior (You)",
                displayName = "Stickman Fighter",
                level = 12,
                score = 38400L,
                xp = 9600,
                completedLevels = 18,
                isCurrentUser = true
            )
        )

        return list
    }
}
