package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.GameMode
import com.example.model.GameScreen
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.ActionBattleScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BossSelectScreen
import com.example.ui.screens.CharacterCustomizationScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.PracticeModeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WorldMapScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF090A0E)),
                    containerColor = Color(0xFF090A0E)
                ) { _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                    ) {
                        MathBrawlApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MathBrawlApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.profileFlow.collectAsState()
    val userProfile by viewModel.userProfileFlow.collectAsState()
    val authState by viewModel.authStateFlow.collectAsState()
    val achievements by viewModel.achievementsFlow.collectAsState()
    val levels by viewModel.levelsFlow.collectAsState()

    AnimatedContent(
        targetState = uiState.currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            GameScreen.MAIN_MENU -> {
                MainMenuScreen(
                    userProfile = userProfile,
                    selectedSkin = uiState.selectedSkin,
                    animationTick = uiState.animationTick,
                    onNavigate = { viewModel.navigateTo(it) },
                    onStartBattle = { mode -> viewModel.startBattle(mode) }
                )
            }
            GameScreen.AUTH -> {
                AuthScreen(
                    authManager = viewModel.authManager,
                    authState = authState,
                    onAuthSuccess = { viewModel.navigateTo(GameScreen.MAIN_MENU) },
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.LEADERBOARD -> {
                LeaderboardScreen(
                    repository = viewModel.leaderboardRepository,
                    currentProfile = userProfile,
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.PROFILE -> {
                ProfileScreen(
                    profile = userProfile,
                    onSignOut = {
                        viewModel.authManager.signOut()
                        viewModel.navigateTo(GameScreen.MAIN_MENU)
                    },
                    onOpenAuth = { viewModel.navigateTo(GameScreen.AUTH) },
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.LEVEL_SELECT -> {
                LevelSelectScreen(
                    levelRecords = levels,
                    onSelectLevel = { levelNum ->
                        viewModel.startBattleLevel(levelNum)
                    },
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.BATTLE -> {
                ActionBattleScreen(
                    engine = viewModel.gameEngine,
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.WORLD_MAP -> {
                WorldMapScreen(
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) },
                    onSelectWorldStage = { worldId ->
                        viewModel.startBattle(GameMode.STORY, worldId = worldId)
                    }
                )
            }
            GameScreen.PRACTICE_CONFIG -> {
                PracticeModeScreen(
                    currentTopic = uiState.practiceTopic,
                    currentDifficulty = uiState.practiceDifficulty,
                    onSelectTopic = { topic -> viewModel.setPracticeConfig(topic, uiState.practiceDifficulty) },
                    onSelectDifficulty = { diff -> viewModel.setPracticeConfig(uiState.practiceTopic, diff) },
                    onStartPractice = { viewModel.startBattle(GameMode.PRACTICE) },
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.BOSS_SELECT -> {
                BossSelectScreen(
                    onSelectBoss = { boss ->
                        viewModel.startBattle(GameMode.BOSS_BATTLES, boss = boss)
                    },
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.CHARACTERS -> {
                CharacterCustomizationScreen(
                    currentSkin = uiState.selectedSkin,
                    playerLevel = profile.level,
                    animationTick = uiState.animationTick,
                    onSelectSkin = { skin -> viewModel.selectSkin(skin) },
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.ACHIEVEMENTS -> {
                AchievementsScreen(
                    achievements = achievements,
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            GameScreen.SETTINGS -> {
                SettingsScreen(
                    audioManager = viewModel.audioManager,
                    onToggleSound = { viewModel.toggleSound() },
                    onToggleBgm = { viewModel.toggleBgm() },
                    onToggleHaptics = { viewModel.toggleHaptics() },
                    onNavigateBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                )
            }
            else -> {
                MainMenuScreen(
                    userProfile = userProfile,
                    selectedSkin = uiState.selectedSkin,
                    animationTick = uiState.animationTick,
                    onNavigate = { viewModel.navigateTo(it) },
                    onStartBattle = { mode -> viewModel.startBattle(mode) }
                )
            }
        }
    }
}
