package com.example.game.systems

import com.example.game.model.EnemyType
import com.example.game.model.MathEntity
import kotlin.random.Random

data class WaveConfig(
    val waveNumber: Int,
    val title: String,
    val subtitle: String,
    val enemyQueue: List<EnemyType>,
    val spawnInterval: Float = 1.2f,
    val isBossWave: Boolean = false,
    val bossType: EnemyType? = null
)

class WaveManager(
    private val mathEntitySystem: MathEntitySystem,
    private val groundY: Float = 720f,
    private val arenaLeft: Float = 100f,
    private val arenaRight: Float = 1500f
) {
    var currentWaveIndex: Int = 1
    var waveState: WaveState = WaveState.PREPARING
    var stateTimer: Float = 2.0f
    private var pendingQueue: MutableList<EnemyType> = mutableListOf()
    private var spawnTimer: Float = 0f
    private var currentConfig: WaveConfig = getWaveConfig(1)

    enum class WaveState {
        PREPARING,
        ACTIVE,
        WAVE_CLEAR,
        BOSS_INTRO,
        GAME_OVER,
        VICTORY
    }

    val currentWaveTitle: String get() = currentConfig.title
    val currentWaveSubtitle: String get() = currentConfig.subtitle
    val isBossActive: Boolean get() = currentConfig.isBossWave

    fun startWave(waveNum: Int, audio: AudioSystem) {
        currentWaveIndex = waveNum
        currentConfig = getWaveConfig(waveNum)
        pendingQueue = currentConfig.enemyQueue.toMutableList()
        waveState = if (currentConfig.isBossWave) WaveState.BOSS_INTRO else WaveState.PREPARING
        stateTimer = if (currentConfig.isBossWave) 2.5f else 1.2f
        spawnTimer = 0.1f

        if (currentConfig.isBossWave) {
            audio.playBossEntrance()
        } else {
            audio.playWaveStart()
        }
    }

    fun update(
        dt: Float,
        activeEnemies: MutableList<MathEntity>,
        audio: AudioSystem
    ) {
        stateTimer -= dt

        when (waveState) {
            WaveState.PREPARING, WaveState.BOSS_INTRO -> {
                if (stateTimer <= 0f) {
                    waveState = WaveState.ACTIVE
                    // Immediately spawn initial arena combatants
                    if (activeEnemies.isEmpty() && pendingQueue.isNotEmpty()) {
                        val initialSpawnCount = if (currentWaveIndex == 1) minOf(3, pendingQueue.size) else minOf(2, pendingQueue.size)
                        for (i in 0 until initialSpawnCount) {
                            val nextType = pendingQueue.removeAt(0)
                            val spawnX = if (i % 2 == 0) arenaRight - (140f + i * 90f) else arenaLeft + (140f + i * 90f)
                            val entity = mathEntitySystem.spawnEntity(nextType, spawnX, groundY)
                            activeEnemies.add(entity)
                        }
                    }
                }
            }
            WaveState.ACTIVE -> {
                // Spawn queued enemies over time
                if (pendingQueue.isNotEmpty()) {
                    spawnTimer -= dt
                    if (spawnTimer <= 0f) {
                        spawnTimer = currentConfig.spawnInterval
                        val nextType = pendingQueue.removeAt(0)
                        val spawnX = if (Random.nextBoolean()) arenaLeft + 80f else arenaRight - 80f
                        val entity = mathEntitySystem.spawnEntity(nextType, spawnX, groundY)
                        activeEnemies.add(entity)
                    }
                } else if (activeEnemies.isEmpty()) {
                    // Wave cleared!
                    waveState = WaveState.WAVE_CLEAR
                    stateTimer = 2.5f
                    audio.playWaveComplete()
                }
            }
            WaveState.WAVE_CLEAR -> {
                if (stateTimer <= 0f) {
                    if (currentWaveIndex < 10) {
                        startWave(currentWaveIndex + 1, audio)
                    } else {
                        waveState = WaveState.VICTORY
                    }
                }
            }
            else -> {}
        }
    }

    fun getWaveConfig(wave: Int): WaveConfig {
        return when (wave) {
            1 -> WaveConfig(
                waveNumber = 1,
                title = "WAVE 01: FIRST BATTLE",
                subtitle = "1 MATH MONSTER + 2 MATH SOLDIERS",
                enemyQueue = listOf(
                    EnemyType.OP_PLUS,        // 1 Math Monster
                    EnemyType.SOLDIER_SIGMA,  // Math Soldier 1 (Σ Heavy Brawler)
                    EnemyType.SOLDIER_PI      // Math Soldier 2 (π Martial Artist)
                ),
                spawnInterval = 1.4f
            )
            2 -> WaveConfig(
                waveNumber = 2,
                title = "WAVE 02: OPERATOR INVASION",
                subtitle = "ADDITION & SUBTRACTION SQUAD",
                enemyQueue = listOf(
                    EnemyType.SOLDIER_THETA,
                    EnemyType.OP_MINUS,
                    EnemyType.SOLDIER_DELTA,
                    EnemyType.OP_EQUALS
                ),
                spawnInterval = 1.3f
            )
            3 -> WaveConfig(
                waveNumber = 3,
                title = "WAVE 03: GEOMETRIC CLASH",
                subtitle = "TRIANGULAR & CIRCULAR BEASTS",
                enemyQueue = listOf(
                    EnemyType.MONSTER_GEOMETRY_DELTA,
                    EnemyType.SOLDIER_SIGMA,
                    EnemyType.MONSTER_GEOMETRY_CIRCLE,
                    EnemyType.NUM_7
                ),
                spawnInterval = 1.2f
            )
            4 -> WaveConfig(
                waveNumber = 4,
                title = "WAVE 04: MULTIPLICATION STORM",
                subtitle = "HIGH SPEED ROTATIONAL ENTITIES",
                enemyQueue = listOf(
                    EnemyType.OP_MULTIPLY,
                    EnemyType.SOLDIER_PI,
                    EnemyType.OP_MULTIPLY,
                    EnemyType.SOLDIER_THETA
                ),
                spawnInterval = 1.2f
            )
            5 -> WaveConfig(
                waveNumber = 5,
                title = "WAVE 05: FRACTION CASCADE",
                subtitle = "DIVISION & DUAL ORBITS",
                enemyQueue = listOf(
                    EnemyType.MONSTER_FRACTION,
                    EnemyType.OP_DIVIDE,
                    EnemyType.NUM_8,
                    EnemyType.SOLDIER_DELTA
                ),
                spawnInterval = 1.1f
            )
            6 -> WaveConfig(
                waveNumber = 6,
                title = "WAVE 06: ALGEBRAIC RESISTANCE",
                subtitle = "VARIABLE STALKERS & BRUTES",
                enemyQueue = listOf(
                    EnemyType.MONSTER_ALGEBRA_X,
                    EnemyType.SOLDIER_SIGMA,
                    EnemyType.NUM_9,
                    EnemyType.OP_ROOT
                ),
                spawnInterval = 1.1f
            )
            7 -> WaveConfig(
                waveNumber = 7,
                title = "WAVE 07: FORMULA CONDUIT",
                subtitle = "EQUATION MATRIX",
                enemyQueue = listOf(
                    EnemyType.MONSTER_EQUATION,
                    EnemyType.SOLDIER_PI,
                    EnemyType.SOLDIER_THETA,
                    EnemyType.OP_PERCENT
                ),
                spawnInterval = 1.0f
            )
            8 -> WaveConfig(
                waveNumber = 8,
                title = "WAVE 08",
                subtitle = "GEOMETRIC MATRIX",
                enemyQueue = listOf(EnemyType.OP_ROOT, EnemyType.NUM_9, EnemyType.OP_DIVIDE, EnemyType.NUM_7, EnemyType.OP_PLUS),
                spawnInterval = 1.0f
            )
            9 -> WaveConfig(
                waveNumber = 9,
                title = "WAVE 09",
                subtitle = "EQUATION VANGUARD",
                enemyQueue = listOf(EnemyType.NUM_8, EnemyType.NUM_9, EnemyType.OP_MULTIPLY, EnemyType.OP_DIVIDE, EnemyType.NUM_0),
                spawnInterval = 0.9f
            )
            10 -> WaveConfig(
                waveNumber = 10,
                title = "WAVE 10: TITAN CONFRONTATION",
                subtitle = "THE INTEGER CORE",
                enemyQueue = listOf(EnemyType.BOSS_INTEGER_CORE),
                isBossWave = true,
                bossType = EnemyType.BOSS_INTEGER_CORE
            )
            else -> WaveConfig(
                waveNumber = wave,
                title = "ENDLESS WAVE $wave",
                subtitle = "HYPER-DIMENSION",
                enemyQueue = List(3 + (wave / 2)) {
                    listOf(
                        EnemyType.NUM_7, EnemyType.NUM_8, EnemyType.NUM_9,
                        EnemyType.OP_MULTIPLY, EnemyType.OP_DIVIDE, EnemyType.OP_ROOT
                    ).random()
                },
                spawnInterval = 0.8f
            )
        }
    }
}
