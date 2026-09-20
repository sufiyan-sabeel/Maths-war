package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.model.Difficulty
import com.example.model.GameMode
import com.example.model.MathTopic
import com.example.ui.components.ArcadeButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeModeScreen(
    currentTopic: MathTopic,
    currentDifficulty: Difficulty,
    onSelectTopic: (MathTopic) -> Unit,
    onSelectDifficulty: (Difficulty) -> Unit,
    onStartPractice: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("practice_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "PRACTICE DOJO",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Zero penalties • Step-by-step solutions • Concept mastery",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Topic Selection Section
            Text(
                text = "1. SELECT MATHEMATICAL TOPIC",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MathTopic.values().forEach { topic ->
                    val isSelected = topic == currentTopic
                    Box(
                        modifier = Modifier
                            .testTag("topic_chip_${topic.name}")
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF00E5FF) else Color(0xFF141520))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF00E5FF) else Color(0xFF282A3A),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectTopic(topic) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = topic.symbol,
                                color = if (isSelected) Color.Black else Color(0xFF00E5FF),
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = topic.displayName,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Difficulty Selection
            Text(
                text = "2. SELECT DIFFICULTY LEVEL",
                color = Color(0xFFFF9100),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Difficulty.values().forEach { diff ->
                    val isSelected = diff == currentDifficulty
                    val diffColor = when (diff) {
                        Difficulty.EASY -> Color(0xFF00E676)
                        Difficulty.MEDIUM -> Color(0xFF00E5FF)
                        Difficulty.HARD -> Color(0xFFFF9100)
                        Difficulty.EXPERT -> Color(0xFFFF1744)
                    }

                    Box(
                        modifier = Modifier
                            .testTag("diff_chip_${diff.name}")
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) diffColor else Color(0xFF141520))
                            .border(
                                1.dp,
                                if (isSelected) diffColor else Color(0xFF282A3A),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectDifficulty(diff) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = diff.name,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${diff.baseTimeLimitSec}s",
                                color = if (isSelected) Color.Black.copy(alpha = 0.7f) else diffColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start Practice Button
            ArcadeButton(
                text = "START PRACTICE SESSION",
                onClick = onStartPractice,
                modifier = Modifier.fillMaxWidth(),
                primaryColor = Color(0xFF00E676),
                icon = Icons.Default.PlayArrow,
                testTag = "start_practice_btn"
            )
        }
    }
}
