package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioManager
import com.example.combat.BossManager
import com.example.combat.CombatEngine
import com.example.combat.WaveManager
import com.example.data.GameRepository
import com.example.data.MathBrawlDatabase
import com.example.data.PlayerProfileEntity
import com.example.math.MathQuestionEngine
import com.example.model.Achievement
import com.example.model.BossDefinition
import com.example.model.Difficulty
import com.example.model.EnemyCombatState
import com.example.model.FloatingCombatText
import com.example.model.GameMode
import com.example.model.GameScreen
import com.example.model.MathParticle
import com.example.model.MathQuestion
import com.example.model.MathTopic
import com.example.model.PlayerCombatState
import com.example.model.StickmanPose
import com.example.model.StickmanSkin
import com.example.model.WorldData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiState(
    val currentScreen: GameScreen = GameScreen.MAIN_MENU,
    val selectedMode: GameMode = GameMode.WAVE,
    val selectedWorldId: Int = 1,
    val currentWave: Int = 1,
    val score: Long = 0L,
    val combo: Int = 0,
    val maxComboSession: Int = 0,
    val totalAnsweredSession: Int = 0,
    val totalCorrectSession: Int = 0,
    val player: PlayerCombatState = PlayerCombatState(),
    val enemy: EnemyCombatState? = null,
    val currentQuestion: MathQuestion? = null,
    val timeRemainingRatio: Float = 1.0f,
    val timeRemainingSec: Int = 12,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val showExplanationDialog: Boolean = false,
    val lastExplanation: String = "",
    val activeParticles: List<MathParticle> = emptyList(),
    val floatingTexts: List<FloatingCombatText> = emptyList(),
    val selectedAnswerIdx: Int? = null,
    val isAnswerCorrect: Boolean? = null,
    val practiceTopic: MathTopic = MathTopic.ADDITION,
    val practiceDifficulty: Difficulty = Difficulty.MEDIUM,
    val activeBoss: BossDefinition? = null,
    val selectedSkin: StickmanSkin = StickmanSkin.ALL_SKINS[0],
    val animationTick: Float = 0f
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val gameEngine = com.example.game.engine.GameEngine()
    private val db = MathBrawlDatabase.getInstance(application)
    private val repository = GameRepository(db.playerDao())
    val audioManager = AudioManager(application)
    private val combatEngine = CombatEngine()
    private val waveManager = WaveManager()
    private val bossManager = BossManager()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val profileFlow: StateFlow<PlayerProfileEntity> = repository.playerProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerProfileEntity())

    val achievementsFlow: StateFlow<List<Achievement>> = repository.achievementsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Achievement.DEFAULT_ACHIEVEMENTS)

    private var combatLoopJob: Job? = null
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val p = repository.getProfile()
            val skin = StickmanSkin.ALL_SKINS.find { it.id == p.selectedSkinId } ?: StickmanSkin.ALL_SKINS[0]
            audioManager.soundEnabled = p.soundEnabled
            audioManager.hapticsEnabled = p.hapticsEnabled
            _uiState.value = _uiState.value.copy(
                selectedSkin = skin,
                player = _uiState.value.player.copy(skin = skin)
            )
        }
        startAnimationTick()
    }

    private fun startAnimationTick() {
        viewModelScope.launch {
            var tick = 0f
            while (isActive) {
                tick += 0.05f
                val state = _uiState.value

                // Age particles
                val agedParticles = state.activeParticles.mapNotNull { p ->
                    if (p.alpha <= 0.05f) null
                    else p.copy(
                        xRatio = p.xRatio + p.vx,
                        yRatio = p.yRatio + p.vy,
                        alpha = p.alpha - 0.035f,
                        rotation = p.rotation + p.vRot
                    )
                }

                // Age floating texts
                val agedTexts = state.floatingTexts.mapNotNull { t ->
                    if (t.alpha <= 0.05f) null
                    else t.copy(
                        yRatio = t.yRatio + t.vy,
                        alpha = t.alpha - 0.03f
                    )
                }

                _uiState.value = state.copy(
                    animationTick = tick,
                    activeParticles = agedParticles,
                    floatingTexts = agedTexts
                )
                delay(33) // ~30 fps tick for math particles
            }
        }
    }

    fun navigateTo(screen: GameScreen) {
        audioManager.playButtonClick()
        if (screen == GameScreen.MAIN_MENU) {
            audioManager.stopBgm()
            stopCombatLoop()
        }
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun setPracticeConfig(topic: MathTopic, difficulty: Difficulty) {
        audioManager.playButtonClick()
        _uiState.value = _uiState.value.copy(
            practiceTopic = topic,
            practiceDifficulty = difficulty
        )
    }

    fun selectSkin(skin: StickmanSkin) {
        audioManager.playButtonClick()
        _uiState.value = _uiState.value.copy(
            selectedSkin = skin,
            player = _uiState.value.player.copy(skin = skin)
        )
        viewModelScope.launch {
            val p = repository.getProfile()
            repository.saveProfile(p.copy(selectedSkinId = skin.id))
        }
    }

    fun toggleSound() {
        val newVal = !audioManager.soundEnabled
        audioManager.soundEnabled = newVal
        audioManager.playButtonClick()
        viewModelScope.launch {
            val p = repository.getProfile()
            repository.saveProfile(p.copy(soundEnabled = newVal))
        }
    }

    fun toggleHaptics() {
        val newVal = !audioManager.hapticsEnabled
        audioManager.hapticsEnabled = newVal
        audioManager.playButtonClick()
        viewModelScope.launch {
            val p = repository.getProfile()
            repository.saveProfile(p.copy(hapticsEnabled = newVal))
        }
    }

    fun toggleBgm() {
        val newVal = !audioManager.bgmEnabled
        audioManager.bgmEnabled = newVal
        if (newVal && _uiState.value.currentScreen == GameScreen.BATTLE) {
            audioManager.startBgm()
        } else {
            audioManager.stopBgm()
        }
        audioManager.playButtonClick()
    }

    fun startBattle(
        mode: GameMode,
        worldId: Int = 1,
        boss: BossDefinition? = null
    ) {
        audioManager.playButtonClick()
        audioManager.startBgm()

        val startWave = when (mode) {
            GameMode.BOSS_BATTLES -> 10
            GameMode.STORY -> ((worldId - 1) * 2 + 1).coerceIn(1, 10)
            else -> 1
        }
        val skin = _uiState.value.selectedSkin
        gameEngine.player.primaryColor = skin.primaryColor
        gameEngine.player.energyColor = skin.auraColor
        gameEngine.resetGame(startWave)

        val waveNumber = startWave
        val enemyState = when (mode) {
            GameMode.BOSS_BATTLES -> {
                val b = boss ?: BossDefinition.ALL_BOSSES[0]
                bossManager.createBossCombatState(b)
            }
            else -> {
                val waveConfig = waveManager.generateWave(waveNumber)
                waveManager.spawnEnemyForWave(waveConfig)
            }
        }

        _uiState.value = _uiState.value.copy(
            currentScreen = GameScreen.BATTLE,
            selectedMode = mode,
            selectedWorldId = worldId,
            currentWave = waveNumber,
            score = 0L,
            combo = 0,
            maxComboSession = 0,
            totalAnsweredSession = 0,
            totalCorrectSession = 0,
            player = PlayerCombatState(skin = _uiState.value.selectedSkin),
            enemy = enemyState,
            activeBoss = boss,
            isPaused = false,
            isGameOver = false,
            isVictory = false,
            activeParticles = emptyList(),
            floatingTexts = emptyList()
        )

        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        val state = _uiState.value
        val question = when (state.selectedMode) {
            GameMode.BOSS_BATTLES -> {
                val boss = state.activeBoss ?: BossDefinition.ALL_BOSSES[0]
                val phase = state.enemy?.currentPhase ?: 1
                bossManager.generateBossPhaseQuestion(boss, phase)
            }
            GameMode.PRACTICE -> {
                MathQuestionEngine.generateQuestion(state.practiceTopic, state.practiceDifficulty)
            }
            GameMode.STORY -> {
                val worldTopic = when (state.selectedWorldId) {
                    1 -> listOf(MathTopic.ADDITION, MathTopic.SUBTRACTION, MathTopic.MULTIPLICATION, MathTopic.DIVISION).random()
                    2 -> listOf(MathTopic.BASIC_ALGEBRA, MathTopic.LINEAR_EQUATIONS).random()
                    3 -> listOf(MathTopic.GEOMETRY, MathTopic.COORDINATES).random()
                    4 -> listOf(MathTopic.ORDER_OF_OPERATIONS, MathTopic.PERCENTAGES).random()
                    else -> listOf(MathTopic.FRACTIONS, MathTopic.ORDER_OF_OPERATIONS, MathTopic.LINEAR_EQUATIONS).random()
                }
                MathQuestionEngine.generateQuestion(worldTopic, Difficulty.MEDIUM)
            }
            else -> { // WAVE or ENDLESS
                val waveConfig = waveManager.generateWave(state.currentWave)
                MathQuestionEngine.generateQuestion(waveConfig.primaryTopic, waveConfig.difficulty)
            }
        }

        _uiState.value = _uiState.value.copy(
            currentQuestion = question,
            timeRemainingSec = question.timeLimitSec,
            timeRemainingRatio = 1.0f,
            selectedAnswerIdx = null,
            isAnswerCorrect = null
        )

        startTimer(question.timeLimitSec)
    }

    private fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val totalMs = seconds * 1000L
            val stepMs = 100L
            var remainingMs = totalMs
            while (remainingMs > 0 && isActive && !_uiState.value.isPaused && !_uiState.value.isGameOver && !_uiState.value.isVictory) {
                delay(stepMs)
                if (!_uiState.value.isPaused) {
                    remainingMs -= stepMs
                    val ratio = (remainingMs.toFloat() / totalMs).coerceIn(0f, 1f)
                    _uiState.value = _uiState.value.copy(
                        timeRemainingRatio = ratio,
                        timeRemainingSec = (remainingMs / 1000).toInt()
                    )
                }
            }
            if (remainingMs <= 0 && isActive && !_uiState.value.isGameOver && !_uiState.value.isVictory) {
                // Time up = incorrect answer
                onAnswerSelected(-1)
            }
        }
    }

    fun onAnswerSelected(choiceIndex: Int) {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        if (state.selectedAnswerIdx != null || state.isGameOver || state.isVictory) return // already answered

        timerJob?.cancel()
        val isCorrect = choiceIndex == question.correctIndex

        _uiState.value = state.copy(
            selectedAnswerIdx = choiceIndex,
            isAnswerCorrect = isCorrect,
            totalAnsweredSession = state.totalAnsweredSession + 1,
            totalCorrectSession = state.totalCorrectSession + (if (isCorrect) 1 else 0),
            lastExplanation = question.explanation
        )

        viewModelScope.launch {
            if (isCorrect) {
                val newCombo = state.combo + 1
                val maxCombo = maxOf(state.maxComboSession, newCombo)
                audioManager.playCorrectAnswer(newCombo)
                if (newCombo == 5) audioManager.playMathRage()
                else if (newCombo == 10) audioManager.playEquationBreaker()
                else if (newCombo % 3 == 0) audioManager.playComboSurge(newCombo)

                val result = combatEngine.processCorrectAnswer(
                    player = state.player,
                    enemy = state.enemy ?: EnemyCombatState("Construct", "Entity", 100f, 100f, 0f, 0f, androidx.compose.ui.graphics.Color.Cyan),
                    currentCombo = state.combo,
                    conceptEffect = question.conceptEffect,
                    timeRemainingRatio = state.timeRemainingRatio,
                    activeParticles = state.activeParticles,
                    activeTexts = state.floatingTexts
                )

                _uiState.value = _uiState.value.copy(
                    player = result.updatedPlayer,
                    enemy = result.updatedEnemy,
                    combo = result.combo,
                    maxComboSession = maxCombo,
                    score = state.score + result.scoreGained,
                    activeParticles = result.particles,
                    floatingTexts = result.floatingTexts
                )

                checkAchievements(newCombo, 1)

                delay(800) // view attack impact

                if (result.isEnemyDefeated) {
                    handleEnemyDefeated()
                } else {
                    // Boss phase check
                    if (state.enemy?.isBoss == true && result.updatedEnemy.hp <= (result.updatedEnemy.maxHp * (result.updatedEnemy.totalPhases - result.updatedEnemy.currentPhase) / result.updatedEnemy.totalPhases)) {
                        advanceBossPhase()
                    } else {
                        // Reset player pose to IDLE and continue
                        _uiState.value = _uiState.value.copy(
                            player = _uiState.value.player.copy(currentPose = StickmanPose.IDLE),
                            enemy = _uiState.value.enemy?.copy(pose = StickmanPose.IDLE)
                        )
                        loadNextQuestion()
                    }
                }
            } else {
                audioManager.playIncorrectAnswer()
                val result = combatEngine.processIncorrectAnswer(
                    player = state.player,
                    enemy = state.enemy ?: EnemyCombatState("Construct", "Entity", 100f, 100f, 0f, 0f, androidx.compose.ui.graphics.Color.Cyan),
                    activeParticles = state.activeParticles,
                    activeTexts = state.floatingTexts
                )

                _uiState.value = _uiState.value.copy(
                    player = result.updatedPlayer,
                    enemy = result.updatedEnemy,
                    combo = 0,
                    activeParticles = result.particles,
                    floatingTexts = result.floatingTexts
                )

                delay(800)

                if (result.isPlayerDefeated) {
                    handleGameOver()
                } else {
                    _uiState.value = _uiState.value.copy(
                        player = _uiState.value.player.copy(currentPose = StickmanPose.IDLE),
                        enemy = _uiState.value.enemy?.copy(pose = StickmanPose.IDLE)
                    )
                    loadNextQuestion()
                }
            }
        }
    }

    private fun advanceBossPhase() {
        val enemy = _uiState.value.enemy ?: return
        val nextPhase = enemy.currentPhase + 1
        audioManager.playBossWarning()
        _uiState.value = _uiState.value.copy(
            enemy = enemy.copy(
                currentPhase = nextPhase,
                shield = enemy.maxShield,
                pose = StickmanPose.SPECIAL_CHANNEL
            ),
            player = _uiState.value.player.copy(currentPose = StickmanPose.BLOCK)
        )
        loadNextQuestion()
    }

    private fun handleEnemyDefeated() {
        val state = _uiState.value
        val isBossFight = state.selectedMode == GameMode.BOSS_BATTLES
        val isStoryClear = state.selectedMode == GameMode.STORY && state.currentWave >= 5

        if (isBossFight || isStoryClear) {
            audioManager.playVictory()
            _uiState.value = _uiState.value.copy(
                isVictory = true,
                player = state.player.copy(currentPose = StickmanPose.VICTORY)
            )
            saveSessionResults(true)
        } else {
            // Next Wave
            val nextWave = state.currentWave + 1
            audioManager.playVictory()
            val waveConfig = waveManager.generateWave(nextWave)
            val newEnemy = waveManager.spawnEnemyForWave(waveConfig)

            _uiState.value = _uiState.value.copy(
                currentWave = nextWave,
                enemy = newEnemy,
                player = state.player.copy(
                    currentPose = StickmanPose.IDLE,
                    hp = minOf(state.player.maxHp, state.player.hp + 25f) // wave clear heal
                )
            )
            loadNextQuestion()
        }
    }

    private fun handleGameOver() {
        audioManager.playDefeat()
        _uiState.value = _uiState.value.copy(
            isGameOver = true,
            player = _uiState.value.player.copy(currentPose = StickmanPose.DEFEAT)
        )
        saveSessionResults(false)
    }

    private fun saveSessionResults(isWin: Boolean) {
        val state = _uiState.value
        viewModelScope.launch {
            val xpEarned = (state.score / 10).toInt() + (if (isWin) 200 else 50)
            repository.recordCombatResult(
                scoreGained = state.score,
                maxComboAchieved = state.maxComboSession,
                answeredCount = state.totalAnsweredSession,
                correctCount = state.totalCorrectSession,
                waveReached = state.currentWave,
                xpGained = xpEarned
            )
        }
    }

    private fun checkAchievements(combo: Int, correctIncrement: Int) {
        viewModelScope.launch {
            if (combo >= 1) repository.updateAchievementProgress("first_blood", 1)
            if (combo >= 3) repository.updateAchievementProgress("combo_3", 3)
            if (combo >= 5) repository.updateAchievementProgress("combo_5", 5)
            if (combo >= 10) repository.updateAchievementProgress("combo_10", 10)
            if (_uiState.value.currentWave >= 5) repository.updateAchievementProgress("wave_5", 5)
            if (_uiState.value.currentWave >= 10) repository.updateAchievementProgress("wave_10", 10)
        }
    }

    fun triggerSpecialButton() {
        val state = _uiState.value
        if (state.enemy == null || state.isGameOver || state.isVictory) return
        if (state.player.specialMeter < 50f && !state.player.isEquationBreakerReady) return

        audioManager.playEquationBreaker()
        val result = combatEngine.triggerSpecialAttack(
            player = state.player,
            enemy = state.enemy,
            activeParticles = state.activeParticles,
            activeTexts = state.floatingTexts
        )

        _uiState.value = state.copy(
            player = result.updatedPlayer,
            enemy = result.updatedEnemy,
            score = state.score + result.scoreGained,
            activeParticles = result.particles,
            floatingTexts = result.floatingTexts
        )

        viewModelScope.launch {
            delay(700)
            if (result.isEnemyDefeated) {
                handleEnemyDefeated()
            } else {
                _uiState.value = _uiState.value.copy(
                    player = _uiState.value.player.copy(currentPose = StickmanPose.IDLE)
                )
            }
        }
    }

    fun pauseGame() {
        audioManager.playButtonClick()
        _uiState.value = _uiState.value.copy(isPaused = true)
    }

    fun resumeGame() {
        audioManager.playButtonClick()
        _uiState.value = _uiState.value.copy(isPaused = false)
    }

    fun restartBattle() {
        audioManager.playButtonClick()
        startBattle(
            mode = _uiState.value.selectedMode,
            worldId = _uiState.value.selectedWorldId,
            boss = _uiState.value.activeBoss
        )
    }

    fun toggleExplanationDialog() {
        audioManager.playButtonClick()
        _uiState.value = _uiState.value.copy(showExplanationDialog = !_uiState.value.showExplanationDialog)
    }

    private fun stopCombatLoop() {
        combatLoopJob?.cancel()
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopCombatLoop()
        audioManager.release()
    }
}
