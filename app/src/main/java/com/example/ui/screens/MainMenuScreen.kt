package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.RankTier
import com.example.data.firebase.UserProfile
import com.example.model.GameMode
import com.example.model.GameScreen
import com.example.model.StickmanPose
import com.example.model.StickmanSkin
import com.example.ui.components.ArcadeButton
import com.example.ui.render.ArenaBackgroundRenderer
import com.example.ui.render.StickmanRenderer

@Composable
fun MainMenuScreen(
    userProfile: UserProfile,
    selectedSkin: StickmanSkin,
    animationTick: Float,
    onNavigate: (GameScreen) -> Unit,
    onStartBattle: (GameMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val rankTier = RankTier.fromRank(userProfile.rank)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        // Horizontal Split Layout: Left (Stickman Combat Arena Preview & Profile Card), Right (Game Modes & Menus)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Panel: Fighter Showcase, Title, & Profile Summary
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Title & Subtitle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MATHS WAR",
                            color = Color(0xFFF0F0F5),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "2D ACTION COMBAT",
                            color = Color(0xFFE67E22),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    // Profile / Identity Tag (Clickable to go to Profile)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0D0E12))
                            .border(1.dp, Color(0x356B7280), RoundedCornerShape(16.dp))
                            .clickable { onNavigate(GameScreen.PROFILE) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("menu_btn_profile_tag")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(rankTier.colorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${userProfile.level}",
                                color = Color(0xFF0D0E12),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = userProfile.username,
                                color = Color(0xFFF0F0F5),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "#${userProfile.rank} • ${rankTier.title.take(6)}",
                                color = Color(rankTier.colorHex),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Interactive Stickman Fighter Showcase Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 4.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        ArenaBackgroundRenderer.drawWorldEnvironment(this, 1, animationTick)
                        StickmanRenderer.drawStickman(
                            scope = this,
                            centerX = size.width * 0.5f,
                            centerY = size.height * 0.52f,
                            scale = 1.25f,
                            pose = StickmanPose.IDLE,
                            color = selectedSkin.primaryColor,
                            auraColor = selectedSkin.auraColor,
                            isFacingRight = true,
                            animationTick = animationTick,
                            isMathRage = false
                        )
                    }
                }

                // Bottom Left Score & Stats Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D0E12))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "TOTAL SCORE", color = Color(0xFF8A8D98), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "${userProfile.totalScore}", color = Color(0xFFE67E22), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "CLEARED LEVELS", color = Color(0xFF8A8D98), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "${userProfile.completedLevels} / 52", color = Color(0xFF2ECC71), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Right Panel: Primary Mode Navigation Grid
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "SELECT ENGAGEMENT",
                    color = Color(0xFF8A8D98),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // Main Campaign Button (52 Levels across 5 Worlds)
                ArcadeButton(
                    text = "CAMPAIGN (52 LEVELS)",
                    onClick = { onNavigate(GameScreen.LEVEL_SELECT) },
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = Color(0xFFE67E22),
                    icon = Icons.Default.PlayArrow,
                    testTag = "btn_campaign"
                )

                // Quick Brawl & Boss Arena Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArcadeButton(
                        text = "QUICK BRAWL",
                        onClick = { onStartBattle(GameMode.WAVE) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.Waves,
                        testTag = "btn_quick_brawl"
                    )
                    ArcadeButton(
                        text = "BOSS ARENA",
                        onClick = { onNavigate(GameScreen.BOSS_SELECT) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.Bolt,
                        testTag = "btn_bosses"
                    )
                }

                // Leaderboard & Practice Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArcadeButton(
                        text = "TOP 300 RANKS",
                        onClick = { onNavigate(GameScreen.LEADERBOARD) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF2980B9),
                        icon = Icons.Default.Leaderboard,
                        testTag = "btn_leaderboard"
                    )
                    ArcadeButton(
                        text = "PRACTICE",
                        onClick = { onNavigate(GameScreen.PRACTICE_CONFIG) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF374151),
                        icon = Icons.Default.FitnessCenter,
                        testTag = "btn_practice"
                    )
                }

                // Characters & Profile Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArcadeButton(
                        text = "SKINS",
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
                        testTag = "btn_trophies"
                    )
                }

                // Settings & Profile Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArcadeButton(
                        text = "PROFILE",
                        onClick = { onNavigate(GameScreen.PROFILE) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF2A2D37),
                        icon = Icons.Default.Person,
                        testTag = "btn_profile"
                    )
                    ArcadeButton(
                        text = "SETTINGS",
                        onClick = { onNavigate(GameScreen.SETTINGS) },
                        modifier = Modifier.weight(1f),
                        primaryColor = Color(0xFF2A2D37),
                        icon = Icons.Default.Settings,
                        testTag = "btn_settings"
                    )
                }
            }
        }
    }
}
