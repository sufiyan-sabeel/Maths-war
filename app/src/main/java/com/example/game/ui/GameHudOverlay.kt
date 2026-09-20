package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.engine.GameStateSnapshot
import com.example.game.systems.WaveManager

@Composable
fun GameHudOverlay(
    snapshot: GameStateSnapshot,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onQuitClick: () -> Unit,
    isPaused: Boolean
) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Player Stats: HP & Shield & Special
            Column(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // HP Bar (Crimson)
                val hpPct = (snapshot.player.hp / snapshot.player.maxHp).coerceIn(0f, 1f)
                val shieldPct = (snapshot.player.shield / snapshot.player.maxShield).coerceIn(0f, 1f)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HP",
                        color = Color(0xFFE74C3C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF2A2D37))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(hpPct)
                                .fillMaxSize()
                                .background(Color(0xFFE74C3C))
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${snapshot.player.hp.toInt()}",
                        color = Color(0xFFF0F0F5),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Shield Bar (Slate Steel)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SH",
                        color = Color(0xFF8A8D98),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF2A2D37))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(shieldPct)
                                .fillMaxSize()
                                .background(Color(0xFF8A8D98))
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${snapshot.player.shield.toInt()}",
                        color = Color(0xFF8A8D98),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Special Energy Bar (Warm Amber)
                val specialPct = (snapshot.player.specialEnergy / snapshot.player.maxSpecialEnergy).coerceIn(0f, 1f)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SP",
                        color = Color(0xFFE67E22),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF2A2D37))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(specialPct)
                                .fillMaxSize()
                                .background(Color(0xFFE67E22))
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = snapshot.player.selectedSpecial.symbol,
                        color = Color(0xFFE67E22),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Wave Information Center
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = snapshot.waveTitle,
                    color = Color(0xFFF0F0F5),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = snapshot.waveSubtitle,
                    color = Color(0xFFE67E22),
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Score & Pause Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SCORE",
                        color = Color(0xFF8A8D98),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${snapshot.score}",
                        color = Color(0xFFF0F0F5),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onPauseClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2A2D37))
                        .testTag("btn_pause")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Color(0xFFF0F0F5),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // BOSS HEALTH BAR (Top center below wave info)
        if (snapshot.isBossActive) {
            val boss = snapshot.enemies.find { it.isBoss }
            if (boss != null) {
                val bossHpPct = (boss.hp / boss.maxHp).coerceIn(0f, 1f)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 56.dp)
                        .width(360.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE014161E))
                        .border(1.dp, Color(0x50C0392B), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = boss.symbolString,
                            color = Color(0xFFF0F0F5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "PHASE ${boss.bossPhase}/4",
                            color = Color(0xFFE67E22),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF2A2D37))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(bossHpPct)
                                .fillMaxSize()
                                .background(Color(0xFFC0392B))
                        )
                    }
                }
            }
        }

        // COMBO COUNTER (Floating on Mid-Left)
        if (snapshot.player.comboCount >= 2) {
            val isRage = snapshot.player.comboCount >= 5
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xC014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "COMBO",
                    color = Color(0xFF8A8D98),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${snapshot.player.comboCount}x",
                    color = if (isRage) Color(0xFFE67E22) else Color(0xFFF0F0F5),
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isRage) 30.sp else 24.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (isRage) {
                    Text(
                        text = "RAGE BOOST",
                        color = Color(0xFFE67E22),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // WAVE ANNOUNCEMENTS OVERLAY
        AnimatedVisibility(
            visible = snapshot.waveState == WaveManager.WaveState.PREPARING ||
                    snapshot.waveState == WaveManager.WaveState.BOSS_INTRO ||
                    snapshot.waveState == WaveManager.WaveState.WAVE_CLEAR,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xF014161E))
                    .border(1.dp, Color(0xFFE67E22), RoundedCornerShape(10.dp))
                    .padding(horizontal = 28.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (snapshot.waveState) {
                            WaveManager.WaveState.WAVE_CLEAR -> "WAVE CLEARED"
                            WaveManager.WaveState.BOSS_INTRO -> "TITAN DETECTED"
                            else -> snapshot.waveTitle
                        },
                        color = if (snapshot.waveState == WaveManager.WaveState.BOSS_INTRO) Color(0xFFE74C3C) else Color(0xFFE67E22),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = snapshot.waveSubtitle,
                        color = Color(0xFFF0F0F5),
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // PAUSE DIALOG
        if (isPaused) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(300.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xF514161E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6B7280))
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PAUSED",
                        color = Color(0xFFF0F0F5),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onResumeClick,
                        modifier = Modifier.fillMaxWidth().testTag("btn_resume"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22))
                    ) {
                        Text("RESUME", color = Color(0xFF14161E), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onRestartClick,
                        modifier = Modifier.fillMaxWidth().testTag("btn_restart"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2D37))
                    ) {
                        Text("RESTART", color = Color(0xFFF0F0F5), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onQuitClick,
                        modifier = Modifier.fillMaxWidth().testTag("btn_quit"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2028))
                    ) {
                        Text("QUIT TO MENU", color = Color(0xFF8A8D98))
                    }
                }
            }
        }

        // GAME OVER DIALOG
        if (snapshot.isGameOver) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(320.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xF5161418),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0392B))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DEFEAT",
                        color = Color(0xFFE74C3C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "MATHEMATICS PREVAILED",
                        color = Color(0xFF8A8D98),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SCORE: ${snapshot.score}",
                        color = Color(0xFFE67E22),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onRestartClick,
                        modifier = Modifier.fillMaxWidth().testTag("btn_gameover_retry"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                    ) {
                        Text("RETRY WAVE", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onQuitClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2D37))
                    ) {
                        Text("MAIN MENU", color = Color(0xFF8A8D98))
                    }
                }
            }
        }

        // VICTORY DIALOG
        if (snapshot.isVictory) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(320.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xF514161E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE67E22))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VICTORY",
                        color = Color(0xFFF0F0F5),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "ALL EQUATIONS PROVED",
                        color = Color(0xFF8A8D98),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SCORE: ${snapshot.score}",
                        color = Color(0xFFE67E22),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onRestartClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22))
                    ) {
                        Text("PLAY AGAIN", color = Color(0xFF14161E), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onQuitClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2D37))
                    ) {
                        Text("MAIN MENU", color = Color(0xFF8A8D98))
                    }
                }
            }
        }
    }
}
