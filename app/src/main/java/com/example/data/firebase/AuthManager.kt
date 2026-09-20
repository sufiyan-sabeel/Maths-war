package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val profile: UserProfile) : AuthState()
    data class Unauthenticated(val message: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.w("AuthManager", "Firebase Auth initialization skipped: ${e.message}")
        null
    }

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.w("AuthManager", "Firestore initialization skipped: ${e.message}")
        null
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentProfile = MutableStateFlow<UserProfile>(
        UserProfile(uid = "local_guest", username = "MathsWarrior", displayName = "Stickman Fighter")
    )
    val currentProfile: StateFlow<UserProfile> = _currentProfile.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = auth?.currentUser
        if (user != null) {
            loadUserProfile(user.uid, user.email ?: "")
        } else {
            _authState.value = AuthState.Unauthenticated()
        }
    }

    fun playAsGuest(username: String = "MathsWarrior") {
        val guest = UserProfile(
            uid = "guest_${System.currentTimeMillis() % 10000}",
            username = username,
            displayName = username,
            rank = 485,
            highestRank = 485
        )
        _currentProfile.value = guest
        _authState.value = AuthState.Authenticated(guest)
    }

    suspend fun signUpWithEmail(email: String, password: String, username: String): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            _authState.value = AuthState.Loading
            try {
                val cleanUsername = username.trim().lowercase()
                if (cleanUsername.length < 3 || cleanUsername.length > 20) {
                    val err = "Username must be between 3 and 20 characters."
                    _authState.value = AuthState.Error(err)
                    return@withContext Result.failure(Exception(err))
                }

                // Verify username uniqueness
                val isAvailable = isUsernameAvailable(cleanUsername)
                if (!isAvailable) {
                    val err = "Username '$cleanUsername' is already taken."
                    _authState.value = AuthState.Error(err)
                    return@withContext Result.failure(Exception(err))
                }

                if (auth == null) {
                    // Offline fallback
                    val guest = UserProfile(
                        uid = "local_${System.currentTimeMillis()}",
                        username = cleanUsername,
                        displayName = username,
                        rank = 450,
                        highestRank = 450
                    )
                    _currentProfile.value = guest
                    _authState.value = AuthState.Authenticated(guest)
                    return@withContext Result.success(guest)
                }

                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("User creation failed")

                val profile = UserProfile(
                    uid = user.uid,
                    username = cleanUsername,
                    displayName = username.trim(),
                    rank = 500,
                    highestRank = 500
                )

                // Save profile to Firestore
                saveProfileToFirestore(profile)
                // Register username reservation
                reserveUsernameInFirestore(cleanUsername, user.uid)

                _currentProfile.value = profile
                _authState.value = AuthState.Authenticated(profile)
                Result.success(profile)
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Sign up failed"
                _authState.value = AuthState.Error(errorMsg)
                Result.failure(e)
            }
        }

    suspend fun loginWithEmail(email: String, password: String): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            _authState.value = AuthState.Loading
            try {
                if (auth == null) {
                    val fallback = UserProfile(
                        uid = "offline_user",
                        username = email.substringBefore("@"),
                        displayName = email.substringBefore("@")
                    )
                    _currentProfile.value = fallback
                    _authState.value = AuthState.Authenticated(fallback)
                    return@withContext Result.success(fallback)
                }

                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user ?: throw Exception("Login failed")

                val profile = fetchProfileFromFirestore(user.uid) ?: UserProfile(
                    uid = user.uid,
                    username = user.email?.substringBefore("@") ?: "Warrior",
                    displayName = user.displayName ?: "Warrior"
                )

                _currentProfile.value = profile
                _authState.value = AuthState.Authenticated(profile)
                Result.success(profile)
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Login failed"
                _authState.value = AuthState.Error(errorMsg)
                Result.failure(e)
            }
        }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (auth != null) {
                auth.sendPasswordResetEmail(email).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean = withContext(Dispatchers.IO) {
        val clean = username.trim().lowercase()
        if (firestore == null) return@withContext true
        try {
            val doc = firestore.collection("usernames").document(clean).get().await()
            !doc.exists()
        } catch (e: Exception) {
            Log.w("AuthManager", "Username check fallback: ${e.message}")
            true
        }
    }

    private suspend fun reserveUsernameInFirestore(username: String, uid: String) {
        firestore?.collection("usernames")?.document(username)
            ?.set(mapOf("uid" to uid, "createdAt" to System.currentTimeMillis()))
            ?.await()
    }

    private suspend fun saveProfileToFirestore(profile: UserProfile) {
        firestore?.collection("users")?.document(profile.uid)
            ?.set(profile.toMap(), SetOptions.merge())
            ?.await()
    }

    private suspend fun fetchProfileFromFirestore(uid: String): UserProfile? {
        if (firestore == null) return null
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists() && doc.data != null) {
                UserProfile.fromMap(doc.data!!)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("AuthManager", "Fetch profile failed: ${e.message}")
            null
        }
    }

    private fun loadUserProfile(uid: String, email: String) {
        _authState.value = AuthState.Loading
        firestore?.collection("users")?.document(uid)?.get()
            ?.addOnSuccessListener { doc ->
                val profile = if (doc.exists() && doc.data != null) {
                    UserProfile.fromMap(doc.data!!)
                } else {
                    UserProfile(
                        uid = uid,
                        username = email.substringBefore("@").ifEmpty { "Warrior" },
                        displayName = "Math Fighter"
                    )
                }
                _currentProfile.value = profile
                _authState.value = AuthState.Authenticated(profile)
            }
            ?.addOnFailureListener {
                val profile = UserProfile(
                    uid = uid,
                    username = email.substringBefore("@").ifEmpty { "Warrior" },
                    displayName = "Math Fighter"
                )
                _currentProfile.value = profile
                _authState.value = AuthState.Authenticated(profile)
            } ?: run {
            val profile = UserProfile(
                uid = uid,
                username = email.substringBefore("@").ifEmpty { "Warrior" },
                displayName = "Math Fighter"
            )
            _currentProfile.value = profile
            _authState.value = AuthState.Authenticated(profile)
        }
    }

    suspend fun updateProfileStats(
        scoreGained: Long,
        xpGained: Int,
        levelCompleted: Int,
        starsEarned: Int
    ) = withContext(Dispatchers.IO) {
        val current = _currentProfile.value
        val newXp = current.xp + xpGained
        val newLevel = 1 + (newXp / 500)
        val newTotalScore = current.totalScore + scoreGained
        val newBestScore = maxOf(current.bestScore, scoreGained)
        val newGamesPlayed = current.gamesPlayed + 1
        val newCompletedLevels = maxOf(current.completedLevels, levelCompleted)
        val newStars = current.stars + starsEarned

        // Calculate dynamic competitive rank
        val simulatedRank = calculateRankFromScore(newTotalScore)
        val highestRank = minOf(current.highestRank, simulatedRank)

        val updated = current.copy(
            level = newLevel,
            xp = newXp,
            totalScore = newTotalScore,
            bestScore = newBestScore,
            gamesPlayed = newGamesPlayed,
            completedLevels = newCompletedLevels,
            stars = newStars,
            rank = simulatedRank,
            highestRank = highestRank
        )

        _currentProfile.value = updated
        if (current.uid != "local_guest" && !current.uid.startsWith("guest_")) {
            saveProfileToFirestore(updated)
        }
    }

    fun calculateRankFromScore(score: Long): Int {
        // Dynamic rank calculation: starting from 500 down to 1 based on accumulated score
        val rank = when {
            score >= 100000 -> (1..5).random()
            score >= 75000 -> 6 + ((100000 - score) / 5000).toInt().coerceIn(0, 4)
            score >= 50000 -> 11 + ((75000 - score) / 1000).toInt().coerceIn(0, 38)
            score >= 35000 -> 50 + ((50000 - score) / 300).toInt().coerceIn(0, 99)
            score >= 20000 -> 150 + ((35000 - score) / 150).toInt().coerceIn(0, 149)
            score >= 10000 -> 300 + ((20000 - score) / 80).toInt().coerceIn(0, 199)
            score >= 5000 -> 500 + ((10000 - score) / 50).toInt().coerceIn(0, 249)
            score >= 2000 -> 750 + ((5000 - score) / 20).toInt().coerceIn(0, 249)
            else -> 1000 + ((2000 - score) / 10).toInt().coerceAtLeast(0)
        }
        return rank.coerceAtLeast(1)
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w("AuthManager", "Sign out error: ${e.message}")
        }
        _currentProfile.value = UserProfile(uid = "local_guest", username = "Guest", displayName = "Guest")
        _authState.value = AuthState.Unauthenticated()
    }
}
