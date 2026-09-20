package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerProfileEntity
import com.example.model.GameMode
import com.example.model.GameScreen
import com.example.model.StickmanPose
import com.example.model.StickmanSkin
import com.example.ui.components.ArcadeButton
import com.example.ui.render.ArenaBackgroundRenderer
import com.example.ui.render.StickmanRenderer

@Composable
fun MainMenuScreen(
    profile: PlayerProfileEntity,
    selectedSkin: StickmanSkin,
    animationTick: Float,
    onNavigate: (GameScreen) -> Unit,
    onStartBattle: (GameMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
    ) {
        // Canvas background with math grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            ArenaBackgroundRenderer.drawWorldEnvironment(this, 1, animationTick)
            // Draw idle preview stickman warrior in center background
            StickmanRenderer.drawStickman(
                scope = this,
                centerX = size.width * 0.5f,
                centerY = size.height * 0.36f,
                scale = 1.1f,
                pose = StickmanPose.IDLE,
                color = selectedSkin.primaryColor,
                auraColor = selectedSkin.auraColor,
                isFacingRight = true,
                animationTick = animationTick,
                isMathRage = false
            )
        }

        // Foreground UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with Player Level & XP Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xD014161E))
                        .border(1.dp, Color(0x356B7280), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE67E22)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${profile.level}",
                            color = Color(0xFF0D0E12),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "LVL ${profile.level}",
                            color = Color(0xFFF0F0F5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${profile.xp % 500}/500 XP",
                            color = Color(0xFF8A8D98),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // High score badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "HIGH SCORE",
                        color = Color(0xFF8A8D98),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${profile.highScore}",
                        color = Color(0xFFE67E22),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Game Logo: MATH//WAR
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MATH//WAR",
                    color = Color(0xFFF0F0F5),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "WHEN NUMBERS FIGHT BACK",
                    color = Color(0xFFE67E22),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(130.dp)) // Leave room for preview stickman

            // Menu Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary PLAY Button (Quick Wave Mode)
                ArcadeButton(
                    text = "PLAY NOW",
                    onClick = { onStartBattle(GameMode.WAVE) },
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = Color(0xFFE67E22),
                    icon = Icons.Default.PlayArrow,
                    testTag = "btn_play"
                )

                // 2-Column Buttons for Modes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ArcadeButton(
                        text = "STORY",
                        onClick = { onNavigate(GameScreen.WORLD_MAP) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.Map,
                        testTag = "btn_story"
                    )
                    ArcadeButton(
                        text = "WAVE",
                        onClick = { onStartBattle(GameMode.WAVE) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.Waves,
                        testTag = "btn_wave"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ArcadeButton(
                        text = "PRACTICE",
                        onClick = { onNavigate(GameScreen.PRACTICE_CONFIG) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.FitnessCenter,
                        testTag = "btn_practice"
                    )
                    ArcadeButton(
                        text = "BOSSES",
                        onClick = { onNavigate(GameScreen.BOSS_SELECT) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.Bolt,
                        testTag = "btn_bosses"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ArcadeButton(
                        text = "CHARACTERS",
                        onClick = { onNavigate(GameScreen.CHARACTERS) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.Person,
                        testTag = "btn_characters"
                    )
                    ArcadeButton(
                        text = "TROPHIES",
                        onClick = { onNavigate(GameScreen.ACHIEVEMENTS) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.EmojiEvents,
                        testTag = "btn_achievements"
                    )
                }

                ArcadeButton(
                    text = "SETTINGS",
                    onClick = { onNavigate(GameScreen.SETTINGS) },
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = Color(0xFF2A2D37),
                    icon = Icons.Default.Settings,
                    testTag = "btn_settings"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "v1.0 // ORIGINAL NATIVE ENGINE // NO ADS",
                color = Color(0xFF6B7280),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
