package com.example

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.combat.CombatEngine
import com.example.combat.WaveManager
import com.example.math.MathQuestionEngine
import com.example.model.Difficulty
import com.example.model.EnemyCombatState
import com.example.model.MathConceptEffect
import com.example.model.MathTopic
import com.example.model.PlayerCombatState
import com.example.model.StickmanPose
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MATHS WAR", appName)
    }

    @Test
    fun `rank tier calculation works across all rank ranges`() {
        assertEquals(com.example.data.firebase.RankTier.CELESTIAL, com.example.data.firebase.RankTier.fromRank(1))
        assertEquals(com.example.data.firebase.RankTier.CELESTIAL, com.example.data.firebase.RankTier.fromRank(10))
        assertEquals(com.example.data.firebase.RankTier.GRANDMASTER, com.example.data.firebase.RankTier.fromRank(11))
        assertEquals(com.example.data.firebase.RankTier.MASTER, com.example.data.firebase.RankTier.fromRank(50))
        assertEquals(com.example.data.firebase.RankTier.DIAMOND, com.example.data.firebase.RankTier.fromRank(150))
        assertEquals(com.example.data.firebase.RankTier.PLATINUM, com.example.data.firebase.RankTier.fromRank(300))
        assertEquals(com.example.data.firebase.RankTier.GOLD, com.example.data.firebase.RankTier.fromRank(500))
        assertEquals(com.example.data.firebase.RankTier.SILVER, com.example.data.firebase.RankTier.fromRank(850))
        assertEquals(com.example.data.firebase.RankTier.BRONZE, com.example.data.firebase.RankTier.fromRank(2500))
    }

    @Test
    fun `auth manager provides guest profile with unique rank`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val authManager = com.example.data.firebase.AuthManager(context)
        authManager.playAsGuest("Hero")
        val profile = authManager.currentProfile.value
        assertEquals("Hero", profile.username)
        assertTrue(profile.rank in 1..500)
    }

    @Test
    fun `physical action game engine initializes with valid arena and player`() {
        val engine = com.example.game.engine.GameEngine()
        val snapshot = engine.getSnapshot()
        assertEquals(1, snapshot.waveIndex)
        assertEquals(100f, snapshot.player.hp, 0.01f)
        assertEquals(30f, snapshot.player.shield, 0.01f)
        assertTrue(snapshot.player.pos.y > 0f)
    }

    @Test
    fun `player controller activates attack combo states`() {
        val player = com.example.game.model.PlayerFighter()
        val controller = com.example.game.systems.PlayerController()
        val input = com.example.game.model.TouchInput().apply { attackTriggered = true }
        val audio = com.example.game.systems.AudioSystem()

        controller.update(0.016f, player, input, audio)
        assertEquals(com.example.game.model.StickmanAction.PUNCH_1, player.action)
        assertTrue(player.attackHitboxActive)
        assertEquals(1, player.comboCount)
    }

    @Test
    fun `math entity system spawns correct number and operator entities`() {
        val system = com.example.game.systems.MathEntitySystem()
        val numEntity = system.spawnEntity(com.example.game.model.EnemyType.NUM_8, 500f, 720f)
        assertEquals("8", numEntity.symbolString)
        assertEquals(140f, numEntity.maxHp, 0.01f)

        val bossEntity = system.spawnEntity(com.example.game.model.EnemyType.BOSS_INTEGER_CORE, 800f, 720f)
        assertTrue(bossEntity.isBoss)
        assertEquals("|Z|", bossEntity.symbolString)
        assertEquals(600f, bossEntity.maxHp, 0.01f)
    }

    @Test
    fun `division operator splits upon defeat`() {
        val system = com.example.game.systems.MathEntitySystem()
        val divideEnemy = system.spawnEntity(com.example.game.model.EnemyType.OP_DIVIDE, 500f, 720f).apply {
            hp = 0f
            isDead = true
        }
        val currentEnemies = mutableListOf<com.example.game.model.MathEntity>()
        system.handleSplits(listOf(divideEnemy), currentEnemies, 720f)
        assertEquals(2, currentEnemies.size)
        assertEquals(com.example.game.model.EnemyType.OP_DIVIDE, currentEnemies[0].type)
        assertEquals(com.example.game.model.EnemyType.OP_DIVIDE, currentEnemies[1].type)
    }

    @Test
    fun `math question engine generates valid questions for all topics`() {
        MathTopic.values().forEach { topic ->
            val question = MathQuestionEngine.generateQuestion(topic, Difficulty.MEDIUM)
            assertNotNull(question.prompt)
            assertTrue(question.choices.isNotEmpty())
            assertTrue(question.correctIndex in question.choices.indices)
            assertTrue(question.formulaDisplay.isNotEmpty())
            assertTrue(question.timeLimitSec > 0)
        }
    }

    @Test
    fun `combat engine processes correct answer and updates combo`() {
        val engine = CombatEngine()
        val player = PlayerCombatState()
        val enemy = EnemyCombatState("Drone", "Sub", 100f, 100f, 20f, 20f, Color.Cyan)

        val result = engine.processCorrectAnswer(
            player = player,
            enemy = enemy,
            currentCombo = 4, // Next will be 5: Math Rage!
            conceptEffect = MathConceptEffect.ADDITION,
            timeRemainingRatio = 0.8f,
            activeParticles = emptyList(),
            activeTexts = emptyList()
        )

        assertEquals(5, result.combo)
        assertTrue(result.updatedPlayer.isMathRage)
        assertTrue(result.updatedEnemy.hp < 100f || result.updatedEnemy.shield < 20f)
    }

    @Test
    fun `combat engine resets combo on miscalculation`() {
        val engine = CombatEngine()
        val player = PlayerCombatState()
        val enemy = EnemyCombatState("Drone", "Sub", 100f, 100f, 0f, 0f, Color.Cyan)

        val result = engine.processIncorrectAnswer(
            player = player,
            enemy = enemy,
            activeParticles = emptyList(),
            activeTexts = emptyList()
        )

        assertEquals(0, result.combo)
        assertTrue(result.updatedPlayer.hp < 100f)
        assertEquals(StickmanPose.HIT_REACTION, result.updatedPlayer.currentPose)
    }

    @Test
    fun `wave manager generates wave and boss configurations`() {
        val waveManager = WaveManager()
        val wave1 = waveManager.generateWave(1)
        assertEquals(1, wave1.waveNumber)
        assertEquals(false, wave1.isBossWave)

        val wave5 = waveManager.generateWave(5)
        assertEquals(5, wave5.waveNumber)
        assertEquals(true, wave5.isBossWave)
        assertNotNull(wave5.bossId)
    }
}
