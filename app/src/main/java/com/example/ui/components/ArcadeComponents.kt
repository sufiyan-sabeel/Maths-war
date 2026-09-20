package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.model.EnemyCombatState
import com.example.model.MathQuestion
import com.example.model.PlayerCombatState

@Composable
fun ArcadeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF00E5FF),
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = "arcade_button"
) {
    Surface(
        modifier = modifier
            .testTag(testTag)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) Color(0xFF14151E) else Color(0xFF0D0E12),
        border = BorderStroke(1.5.dp, if (enabled) primaryColor.copy(alpha = 0.8f) else Color(0x306B7280)),
        shadowElevation = if (enabled) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) primaryColor else Color(0xFF6B7280),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text.uppercase(),
                color = if (enabled) Color.White else Color(0xFF6B7280),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun CombatHudHeader(
    waveNumber: Int,
    score: Long,
    combo: Int,
    accuracyPct: Int,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Wave & Accuracy
        Column {
            Text(
                text = "WAVE $waveNumber",
                color = Color(0xFF00E5FF),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "ACCURACY $accuracyPct%",
                color = Color(0xFF9E9E9E),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Combo indicator
        if (combo > 0) {
            val comboColor = when {
                combo >= 10 -> Color(0xFFFF1744)
                combo >= 5 -> Color(0xFFFF9100)
                else -> Color(0xFF00E5FF)
            }
            val comboText = when {
                combo >= 10 -> "EQUATION BREAKER x$combo"
                combo >= 5 -> "MATH RAGE x$combo"
                else -> "COMBO x$combo"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(comboColor.copy(alpha = 0.2f))
                    .border(1.dp, comboColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = comboText,
                    color = comboColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Score & Pause
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "SCORE",
                    color = Color(0xFF757575),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "$score",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .testTag("pause_button")
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F202C))
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun HealthAndEnergyBars(
    player: PlayerCombatState,
    enemy: EnemyCombatState?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Player stats (Left)
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "PLAYER",
                    color = player.skin.primaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${player.hp.toInt()}/${player.maxHp.toInt()}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            // HP Bar
            LinearProgressIndicator(
                progress = { (player.hp / player.maxHp).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = player.skin.primaryColor,
                trackColor = Color(0xFF20202A),
            )
            // Special / Equation Meter
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { (player.specialMeter / player.maxSpecial).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFFFFD600),
                trackColor = Color(0xFF1E1E26),
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Enemy stats (Right)
        if (enemy != null) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${enemy.hp.toInt()}/${enemy.maxHp.toInt()}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (enemy.isBoss) "${enemy.name} (P${enemy.currentPhase})" else enemy.name,
                        color = enemy.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { (enemy.hp / enemy.maxHp).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = enemy.color,
                    trackColor = Color(0xFF20202A),
                )
                if (enemy.maxShield > 0f) {
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { (enemy.shield / enemy.maxShield).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFF1E1E26),
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionPromptCard(
    question: MathQuestion,
    timeRatio: Float,
    timeSec: Int,
    modifier: Modifier = Modifier
) {
    val timerColor = when {
        timeRatio > 0.5f -> Color(0xFF00E5FF)
        timeRatio > 0.25f -> Color(0xFFFF9100)
        else -> Color(0xFFFF1744)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF12131D),
        border = BorderStroke(1.dp, Color(0xFF2A2B3D)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with topic and timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${question.topic.displayName.uppercase()} // ${question.difficulty.name}",
                    color = Color(0xFF8C8D9E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${timeSec}s",
                    color = timerColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Time bar
            LinearProgressIndicator(
                progress = { timeRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = timerColor,
                trackColor = Color(0xFF1A1A24),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.prompt,
                color = Color(0xFFB0B0C0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Formula Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF090A10))
                    .border(1.dp, Color(0xFF1E2030), RoundedCornerShape(10.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.formulaDisplay,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AnswerButtonsGrid(
    choices: List<String>,
    correctIndex: Int,
    selectedIndex: Int?,
    onChoiceClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1 (choices 0 and 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0..1) {
                if (i < choices.size) {
                    AnswerCard(
                        text = choices[i],
                        idx = i,
                        correctIdx = correctIndex,
                        selectedIdx = selectedIndex,
                        onClick = { onChoiceClick(i) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        // Row 2 (choices 2 and 3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 2..3) {
                if (i < choices.size) {
                    AnswerCard(
                        text = choices[i],
                        idx = i,
                        correctIdx = correctIndex,
                        selectedIdx = selectedIndex,
                        onClick = { onChoiceClick(i) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerCard(
    text: String,
    idx: Int,
    correctIdx: Int,
    selectedIdx: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedIdx == idx
    val hasAnswered = selectedIdx != null
    val isCorrect = idx == correctIdx

    val (bgColor, borderColor, textColor) = when {
        !hasAnswered -> Triple(Color(0xFF141520), Color(0xFF282A3A), Color.White)
        isSelected && isCorrect -> Triple(Color(0xFF00C853).copy(alpha = 0.25f), Color(0xFF00E676), Color(0xFF00E676))
        isSelected && !isCorrect -> Triple(Color(0xFFFF1744).copy(alpha = 0.25f), Color(0xFFFF5252), Color(0xFFFF5252))
        hasAnswered && isCorrect -> Triple(Color(0xFF00C853).copy(alpha = 0.20f), Color(0xFF00E676), Color(0xFF00E676))
        else -> Triple(Color(0xFF10111A), Color(0xFF1C1D28), Color(0xFF6B6C7E))
    }

    Surface(
        modifier = modifier
            .testTag("answer_choice_$idx")
            .height(58.dp)
            .clickable(enabled = selectedIdx == null) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
            if (hasAnswered && isCorrect) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correct",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(18.dp)
                )
            } else if (hasAnswered && isSelected && !isCorrect) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Incorrect",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SpecialAttackButton(
    isReady: Boolean,
    meterRatio: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "special")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = modifier
            .testTag("special_attack_button")
            .scale(if (isReady) pulseScale else 1f)
            .clickable(enabled = isReady) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isReady) Color(0xFF332A00) else Color(0xFF141520),
        border = BorderStroke(
            1.5.dp,
            if (isReady) Color(0xFFFFD600) else Color(0xFF282A3A)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Special",
                tint = if (isReady) Color(0xFFFFD600) else Color(0xFF6B6C7E),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isReady) "EQUATION BREAKER READY!" else "SPECIAL ${(meterRatio * 100).toInt()}%",
                color = if (isReady) Color(0xFFFFD600) else Color(0xFF8C8D9E),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
