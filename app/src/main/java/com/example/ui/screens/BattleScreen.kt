package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameScreen
import com.example.model.StickmanPose
import com.example.ui.components.AnswerButtonsGrid
import com.example.ui.components.ArcadeButton
import com.example.ui.components.CombatHudHeader
import com.example.ui.components.HealthAndEnergyBars
import com.example.ui.components.QuestionPromptCard
import com.example.ui.components.SpecialAttackButton
import com.example.ui.render.ArenaBackgroundRenderer
import com.example.ui.render.CombatEffectsRenderer
import com.example.ui.render.StickmanRenderer
import com.example.viewmodel.GameUiState

@Composable
fun BattleScreen(
    state: GameUiState,
    onAnswerClick: (Int) -> Unit,
    onSpecialClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onQuitClick: () -> Unit,
    onToggleExplanation: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onPauseClick()
    }

    val accuracy = if (state.totalAnsweredSession > 0) {
        ((state.totalCorrectSession.toFloat() / state.totalAnsweredSession) * 100).toInt()
    } else 100

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0E))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top HUD
            Column {
                CombatHudHeader(
                    waveNumber = state.currentWave,
                    score = state.score,
                    combo = state.combo,
                    accuracyPct = accuracy,
                    onPauseClick = onPauseClick
                )
                HealthAndEnergyBars(
                    player = state.player,
                    enemy = state.enemy
                )
            }

            // Central Dynamic Combat Arena Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // World environment
                    ArenaBackgroundRenderer.drawWorldEnvironment(
                        scope = this,
                        worldId = state.selectedWorldId,
                        time = state.animationTick
                    )

                    val arenaFloorY = size.height * 0.72f
                    val playerX = size.width * 0.28f
                    val enemyX = size.width * 0.74f

                    // Draw Player Stickman
                    StickmanRenderer.drawStickman(
                        scope = this,
                        centerX = playerX,
                        centerY = arenaFloorY - 20f,
                        scale = 1.05f,
                        pose = state.player.currentPose,
                        color = state.player.skin.primaryColor,
                        auraColor = state.player.skin.auraColor,
                        isFacingRight = true,
                        animationTick = state.animationTick,
                        isMathRage = state.player.isMathRage
                    )

                    // Draw Enemy Stickman
                    val enemy = state.enemy
                    if (enemy != null) {
                        StickmanRenderer.drawStickman(
                            scope = this,
                            centerX = enemyX,
                            centerY = arenaFloorY - 20f,
                            scale = if (enemy.isBoss) 1.25f else 1.05f,
                            pose = enemy.pose,
                            color = enemy.color,
                            auraColor = enemy.color,
                            isFacingRight = false,
                            animationTick = state.animationTick,
                            isMathRage = enemy.isBoss && enemy.currentPhase > 1
                        )
                    }

                    // Draw particles and floating damage text
                    CombatEffectsRenderer.drawCombatEffects(
                        scope = this,
                        particles = state.activeParticles,
                        texts = state.floatingTexts
                    )
                }

                // Special Attack Button when ready
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SpecialAttackButton(
                        isReady = state.player.isEquationBreakerReady,
                        meterRatio = (state.player.specialMeter / state.player.maxSpecial).coerceIn(0f, 1f),
                        onClick = onSpecialClick
                    )
                }
            }

            // Bottom Interaction: Question & 4 Answers
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C0D15))
                    .padding(bottom = 12.dp)
            ) {
                if (state.currentQuestion != null) {
                    QuestionPromptCard(
                        question = state.currentQuestion,
                        timeRatio = state.timeRemainingRatio,
                        timeSec = state.timeRemainingSec
                    )
                    AnswerButtonsGrid(
                        choices = state.currentQuestion.choices,
                        correctIndex = state.currentQuestion.correctIndex,
                        selectedIndex = state.selectedAnswerIdx,
                        onChoiceClick = onAnswerClick
                    )
                }
            }
        }

        // Pause Menu Dialog
        if (state.isPaused) {
            AlertDialog(
                onDismissRequest = onResumeClick,
                containerColor = Color(0xFF141520),
                title = {
                    Text(
                        text = "COMBAT PAUSED",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Score: ${state.score}\nWave: ${state.currentWave}\nCombo: x${state.combo}",
                            color = Color(0xFFB0B0C0),
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onResumeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                    ) {
                        Text("RESUME", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Row {
                        OutlinedButton(onClick = onRestartClick) {
                            Text("RESTART", color = Color(0xFFFF9100))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = onQuitClick) {
                            Text("MENU", color = Color(0xFFFF5252))
                        }
                    }
                }
            )
        }

        // Game Over Overlay
        AnimatedVisibility(
            visible = state.isGameOver,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF141522),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DEFEAT",
                            color = Color(0xFFFF1744),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Energy depleted in Wave ${state.currentWave}",
                            color = Color(0xFF9E9E9E),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0A0B10))
                                .padding(16.dp)
                        ) {
                            Text("Final Score: ${state.score}", color = Color.White, fontFamily = FontFamily.Monospace)
                            Text("Max Combo: x${state.maxComboSession}", color = Color(0xFFFF9100), fontFamily = FontFamily.Monospace)
                            Text("Accuracy: $accuracy%", color = Color(0xFF00E5FF), fontFamily = FontFamily.Monospace)
                            Text("Questions Solved: ${state.totalCorrectSession}/${state.totalAnsweredSession}", color = Color.White, fontFamily = FontFamily.Monospace)
                        }

                        if (state.lastExplanation.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Last Solution: ${state.lastExplanation}",
                                color = Color(0xFFB0BEC5),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onRestartClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                            ) {
                                Text("RETRY", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onQuitClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282A3A))
                            ) {
                                Text("MENU", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Victory Overlay
        AnimatedVisibility(
            visible = state.isVictory,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF141A22),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VICTORY!",
                            color = Color(0xFF00E676),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Q.E.D. — Mathematical Proof Complete!",
                            color = Color(0xFF80CBC4),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0A0F12))
                                .padding(16.dp)
                        ) {
                            Text("Total Score: ${state.score}", color = Color.White, fontFamily = FontFamily.Monospace)
                            Text("Max Combo: x${state.maxComboSession}", color = Color(0xFFFFD600), fontFamily = FontFamily.Monospace)
                            Text("Accuracy: $accuracy%", color = Color(0xFF00E676), fontFamily = FontFamily.Monospace)
                            Text("XP Gained: +${(state.score / 10) + 200}", color = Color(0xFF00E5FF), fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onRestartClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                            ) {
                                Text("PLAY AGAIN", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onQuitClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282A3A))
                            ) {
                                Text("MENU", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
