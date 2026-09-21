package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelRecord
import com.example.game.levels.LevelDefinitions

@Composable
fun LevelSelectScreen(
    levelRecords: List<LevelRecord>,
    onSelectLevel: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedChapter by remember { mutableIntStateOf(1) }

    val recordMap = remember(levelRecords) {
        levelRecords.associateBy { it.levelNumber }
    }

    val chapters = listOf(
        1 to "WORLD 1: INTEGERS",
        2 to "WORLD 2: OPERATORS",
        3 to "WORLD 3: GEOMETRY",
        4 to "WORLD 4: ALGEBRA",
        5 to "WORLD 5: CALCULUS"
    )

    val currentChapterLevels = remember(selectedChapter) {
        val start = (selectedChapter - 1) * 10 + 1
        val end = selectedChapter * 10
        (start..end).map { lvl ->
            LevelDefinitions.getLevel(lvl)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A0E))
            .padding(16.dp)
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
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E222D))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "CAMPAIGN BATTLE SECTORS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFF0F0F5),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "50 HANDCRAFTED MATHEMATICAL COMBAT STAGES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE67E22)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // World/Chapter Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedChapter - 1,
            containerColor = Color(0xFF141722),
            contentColor = Color(0xFFE67E22),
            edgePadding = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            chapters.forEach { (chNum, chName) ->
                Tab(
                    selected = selectedChapter == chNum,
                    onClick = { selectedChapter = chNum },
                    text = {
                        Text(
                            text = chName,
                            fontSize = 12.sp,
                            fontWeight = if (selectedChapter == chNum) FontWeight.Black else FontWeight.Normal,
                            color = if (selectedChapter == chNum) Color(0xFFE67E22) else Color(0xFF888899)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Levels Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(currentChapterLevels) { levelDef ->
                val rec = recordMap[levelDef.levelNumber]
                val isUnlocked = rec?.isUnlocked ?: (levelDef.levelNumber == 1)
                val stars = rec?.stars ?: 0
                val isBoss = levelDef.isBossLevel

                LevelCard(
                    levelDef = levelDef,
                    isUnlocked = isUnlocked,
                    stars = stars,
                    highScore = rec?.highScore ?: 0L,
                    onClick = {
                        if (isUnlocked) {
                            onSelectLevel(levelDef.levelNumber)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelCard(
    levelDef: com.example.game.levels.LevelDefinition,
    isUnlocked: Boolean,
    stars: Int,
    highScore: Long,
    onClick: () -> Unit
) {
    val bgBrush = if (isUnlocked) {
        if (levelDef.isBossLevel) {
            Brush.linearGradient(listOf(Color(0xFF3B1518), Color(0xFF1C0E12)))
        } else {
            Brush.linearGradient(listOf(Color(0xFF1A1F2C), Color(0xFF121520)))
        }
    } else {
        Brush.linearGradient(listOf(Color(0xFF10121A), Color(0xFF0C0E14)))
    }

    val borderColor = when {
        !isUnlocked -> Color(0xFF222533)
        levelDef.isBossLevel -> Color(0xFFE74C3C)
        stars == 3 -> Color(0xFFFFD700)
        else -> Color(0xFF33384D)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STAGE ${levelDef.levelNumber}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isUnlocked) (if (levelDef.isBossLevel) Color(0xFFFF6B6B) else Color.White) else Color(0xFF555566)
                )

                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFF666677),
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Row {
                        repeat(3) { starIdx ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (starIdx < stars) Color(0xFFFFD700) else Color(0xFF33384D),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = levelDef.levelSubtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Color(0xFFE67E22) else Color(0xFF444455),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (levelDef.newFeatureIntro.isNotEmpty()) {
                Text(
                    text = levelDef.newFeatureIntro,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFAAAAAA),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (isUnlocked && highScore > 0) {
                Text(
                    text = "BEST: $highScore PTS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF2ECC71)
                )
            }
        }
    }
}
