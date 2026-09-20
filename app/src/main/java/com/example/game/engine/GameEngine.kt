package com.example.game.engine

import com.example.game.levels.LevelDefinition
import com.example.game.levels.LevelDefinitions
import com.example.game.model.ArenaType
import com.example.game.model.CameraState
import com.example.game.model.EnemyType
import com.example.game.model.ImpactEffect
import com.example.game.model.MathEntity
import com.example.game.model.MathParticle
import com.example.game.model.PlayerFighter
import com.example.game.model.Projectile
import com.example.game.model.SpecialAttackType
import com.example.game.model.StickmanAction
import com.example.game.model.TouchInput
import com.example.game.model.Vec2
import com.example.game.systems.AudioSystem
import com.example.game.systems.BossManager
import com.example.game.systems.CameraSystem
import com.example.game.systems.CollisionSystem
import com.example.game.systems.CombatSystem
import com.example.game.systems.EnemyAIController
import com.example.game.systems.MathEntitySystem
import com.example.game.systems.ParticleSystem
import com.example.game.systems.PhysicsSystem
import com.example.game.systems.PlayerController
import com.example.game.systems.WaveManager

data class GameStateSnapshot(
    val player: PlayerFighter,
    val enemies: List<MathEntity>,
    val projectiles: List<Projectile>,
    val particles: List<MathParticle>,
    val impacts: List<ImpactEffect>,
    val camera: CameraState,
    val waveIndex: Int,
    val waveTitle: String,
    val waveSubtitle: String,
    val waveState: WaveManager.WaveState,
    val waveStateTimer: Float,
    val score: Long,
    val isGameOver: Boolean,
    val isVictory: Boolean,
    val isBossActive: Boolean,
    val levelNumber: Int = 1,
    val chapter: Int = 1,
    val chapterTitle: String = "CHAPTER 1",
    val levelTitle: String = "FIRST DIGITS",
    val levelSubtitle: String = "NUMERAL AWAKENING",
    val arenaType: ArenaType = ArenaType.NUMBER_LAB,
    val newFeatureIntro: String = "",
    val targetScoreFor3Stars: Long = 1000L,
    val levelElapsedSec: Float = 0f
)

class GameEngine {

    val audio: AudioSystem = AudioSystem()
    val camera: CameraSystem = CameraSystem()

    private val physics: PhysicsSystem = PhysicsSystem()
    private val collision: CollisionSystem = CollisionSystem()
    private val combat: CombatSystem = CombatSystem()
    private val playerController: PlayerController = PlayerController()
    private val enemyAI: EnemyAIController = EnemyAIController()
    private val mathEntitySystem: MathEntitySystem = MathEntitySystem()
    val waveManager: WaveManager = WaveManager(mathEntitySystem)
    val bossManager: BossManager = BossManager()
    private val particleSystem: ParticleSystem = ParticleSystem()

    val player: PlayerFighter = PlayerFighter()
    val enemies: MutableList<MathEntity> = mutableListOf()
    val projectiles: MutableList<Projectile> = mutableListOf()
    val particles: MutableList<MathParticle> = mutableListOf()
    val impacts: MutableList<ImpactEffect> = mutableListOf()

    val currentInput: TouchInput = TouchInput()

    var score: Long = 0L
    var isPaused: Boolean = false
    var isGameOver: Boolean = false
    var isVictory: Boolean = false
    var levelElapsedSec: Float = 0f
    private var lastRecordedClearedWave: Int = -1

    var onLevelCompletedCallback: ((levelNum: Int, stars: Int, score: Long, timeSec: Float) -> Unit)? = null

    init {
        resetGame()
    }

    fun resetGame(startWave: Int = 1) {
        player.pos = Vec2(350f, physics.groundY)
        player.vel = Vec2(0f, 0f)
        player.hp = player.maxHp
        player.shield = player.maxShield
        player.specialEnergy = 30f
        player.comboCount = 0
        player.action = StickmanAction.IDLE

        enemies.clear()
        projectiles.clear()
        particles.clear()
        impacts.clear()

        isGameOver = false
        isVictory = false
        isPaused = false
        score = 0L
        levelElapsedSec = 0f
        lastRecordedClearedWave = -1

        camera.state.pos = Vec2(350f, physics.groundY - 120f)
        camera.state.zoom = 1.0f
        camera.state.targetZoom = 1.0f
        camera.state.shakeTimer = 0f
        camera.state.shakeIntensity = 0f

        waveManager.startWave(startWave, audio)
        physics.gravityModifier = waveManager.currentLevelDef.gravityModifier
    }

    fun update(dt: Float) {
        if (isPaused || isGameOver || isVictory) return

        levelElapsedSec += dt
        physics.gravityModifier = waveManager.currentLevelDef.gravityModifier

        // 1. Hit-stop freeze frame for impact feeling
        if (combat.hitStopTimer > 0f) {
            combat.hitStopTimer -= dt
            return
        }

        // 2. Special Attack Input Check
        if (currentInput.specialTriggered) {
            currentInput.specialTriggered = false
            combat.castSpecialAttack(player, projectiles, particles, impacts, audio, camera)
        }

        // 3. Player Update via Controller
        playerController.update(dt, player, currentInput, audio)

        // 4. Enemy AI Update
        enemyAI.update(dt, enemies, player, projectiles, audio)

        // 5. Physics Update
        physics.update(dt, player, enemies, projectiles)

        // 6. Collision System & Combat Resolution
        collision.checkCollisions(player, enemies, projectiles) { hitEvent ->
            combat.handleHit(hitEvent, player, particles, impacts, camera, audio)
            if (hitEvent.isPlayerAttacker) {
                score += (hitEvent.damage * (1 + player.comboCount * 0.1f)).toLong()
            }
        }

        // 7. Check Player Defeat
        if (player.hp <= 0f && !isGameOver) {
            isGameOver = true
            player.action = StickmanAction.DEFEAT
            audio.playDefeat()
        }

        // 8. Math Entity Transformations / Splits
        val deadEnemies = enemies.filter { it.isDead }
        if (deadEnemies.isNotEmpty()) {
            mathEntitySystem.handleSplits(deadEnemies, enemies, physics.groundY)
            enemies.removeAll(deadEnemies)
        }

        // 9. Wave Manager Update
        waveManager.update(dt, enemies, audio)
        if (waveManager.waveState == WaveManager.WaveState.WAVE_CLEAR || waveManager.waveState == WaveManager.WaveState.VICTORY) {
            if (lastRecordedClearedWave != waveManager.currentWaveIndex) {
                lastRecordedClearedWave = waveManager.currentWaveIndex
                val targetScore = waveManager.currentLevelDef.targetScoreFor3Stars
                val stars = when {
                    score >= targetScore && (player.hp / player.maxHp) >= 0.4f -> 3
                    score >= (targetScore * 0.65f).toLong() -> 2
                    else -> 1
                }
                onLevelCompletedCallback?.invoke(waveManager.currentWaveIndex, stars, score, levelElapsedSec)
            }
        }
        if (waveManager.waveState == WaveManager.WaveState.VICTORY) {
            isVictory = true
            player.action = StickmanAction.VICTORY
            audio.playVictory()
        }

        // 10. Combat Combo Timers
        combat.updateCombo(dt, player)

        // 11. Particles & Impact Effects
        particleSystem.update(dt, particles, impacts)

        // 12. Camera Tracking
        camera.update(dt, player, waveManager.isBossActive)
    }

    fun selectSpecial(special: SpecialAttackType) {
        player.selectedSpecial = special
    }

    fun getSnapshot(): GameStateSnapshot {
        val def = waveManager.currentLevelDef
        return GameStateSnapshot(
            player = player.copy(),
            enemies = enemies.map { it.copy() },
            projectiles = projectiles.map { it.copy() },
            particles = particles.map { it.copy() },
            impacts = impacts.map { it.copy() },
            camera = camera.state.copy(),
            waveIndex = waveManager.currentWaveIndex,
            waveTitle = waveManager.currentWaveTitle,
            waveSubtitle = waveManager.currentWaveSubtitle,
            waveState = waveManager.waveState,
            waveStateTimer = waveManager.stateTimer,
            score = score,
            isGameOver = isGameOver,
            isVictory = isVictory,
            isBossActive = waveManager.isBossActive,
            levelNumber = def.levelNumber,
            chapter = def.chapter,
            chapterTitle = def.chapterTitle,
            levelTitle = def.title,
            levelSubtitle = def.subtitle,
            arenaType = def.arenaType,
            newFeatureIntro = def.newFeatureIntro,
            targetScoreFor3Stars = def.targetScoreFor3Stars,
            levelElapsedSec = levelElapsedSec
        )
    }
}
