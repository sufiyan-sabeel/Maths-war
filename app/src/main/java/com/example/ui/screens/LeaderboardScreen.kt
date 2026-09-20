package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.data.firebase.LeaderboardEntry
import com.example.data.firebase.LeaderboardRepository
import com.example.data.firebase.RankTier
import com.example.data.firebase.UserProfile
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(
    repository: LeaderboardRepository,
    currentProfile: UserProfile,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var entries by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun refresh() {
        coroutineScope.launch {
            isLoading = true
            entries = repository.getTop300Leaderboard(currentProfile.uid)
            isLoading = false
        }
    }

    LaunchedEffect(currentProfile.uid) {
        refresh()
    }

    val currentUserEntry = entries.find { it.isCurrentUser } ?: LeaderboardEntry(
        rank = currentProfile.rank,
        uid = currentProfile.uid,
        username = currentProfile.username,
        displayName = currentProfile.displayName,
        level = currentProfile.level,
        score = currentProfile.totalScore,
        xp = currentProfile.xp,
        completedLevels = currentProfile.completedLevels,
        isCurrentUser = true
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        // Landscape Split: Left (User Rank & Tiers), Right (Top 300 Scrollable Table)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Panel: Current User Standing & Rank Progression Tier
            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with Back Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0D0E12))
                            .border(1.dp, Color(0x356B7280), RoundedCornerShape(6.dp))
                            .testTag("leaderboard_btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFF0F0F5),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "GLOBAL STANDING",
                            color = Color(0xFFE67E22),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "TOP 300 WARRIORS",
                            color = Color(0xFFF0F0F5),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Current User Rank Highlight Card
                val userTier = RankTier.fromRank(currentUserEntry.rank)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x30E67E22))
                        .border(1.dp, Color(userTier.colorHex), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YOUR GLOBAL POSITION",
                        color = Color(0xFFB0B3BC),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "#${currentUserEntry.rank}",
                        color = Color(userTier.colorHex),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = userTier.title,
                        color = Color(0xFFF0F0F5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(text = "WARRIOR", color = Color(0xFF8A8D98), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(text = currentUserEntry.username, color = Color(0xFFF0F0F5), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "TOTAL SCORE", color = Color(0xFF8A8D98), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "${currentUserEntry.score} PTS", color = Color(0xFFE67E22), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Competitive Tiers Guide
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D0E12))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "COMPETITIVE TIERS",
                        color = Color(0xFF8A8D98),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "CELESTIAL (#1-#10)", color = Color(0xFFE67E22), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "DIAMOND (#150-#299)", color = Color(0xFF2980B9), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "GRANDMASTER (#11-#49)", color = Color(0xFFD35400), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "PLATINUM (#300-#499)", color = Color(0xFF16A085), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Right Panel: Scrollable Top 300 Table
            Column(
                modifier = Modifier
                    .weight(1.25f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                // Table Header & Refresh Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "RANK", color = Color(0xFF8A8D98), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.width(48.dp))
                        Text(text = "WARRIOR", color = Color(0xFF8A8D98), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                        Text(text = "LVL", color = Color(0xFF8A8D98), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.width(40.dp))
                        Text(text = "SCORE", color = Color(0xFF8A8D98), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.width(70.dp), textAlign = TextAlign.End)
                    }

                    IconButton(
                        onClick = { refresh() },
                        modifier = Modifier.size(28.dp).testTag("leaderboard_btn_refresh")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFE67E22), strokeWidth = 2.dp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(entries) { entry ->
                            val tier = RankTier.fromRank(entry.rank)
                            val isTop3 = entry.rank in 1..3

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            entry.isCurrentUser -> Color(0x35E67E22)
                                            isTop3 -> Color(0x20374151)
                                            else -> Color(0xFF0D0E12)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (entry.isCurrentUser) Color(0xFFE67E22) else Color(0x206B7280),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Rank Number
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.width(48.dp)
                                ) {
                                    if (isTop3) {
                                        Icon(
                                            Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = when (entry.rank) {
                                                1 -> Color(0xFFF1C40F)
                                                2 -> Color(0xFFBDC3C7)
                                                else -> Color(0xFFE67E22)
                                            },
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = "#${entry.rank}",
                                        color = if (isTop3 || entry.isCurrentUser) Color(0xFFF0F0F5) else Color(0xFF8A8D98),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Warrior Username
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = entry.username,
                                        color = if (entry.isCurrentUser) Color(0xFFE67E22) else Color(0xFFF0F0F5),
                                        fontSize = 11.sp,
                                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (entry.isCurrentUser) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "(YOU)",
                                            color = Color(0xFFE67E22),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                // Level
                                Text(
                                    text = "L${entry.level}",
                                    color = Color(0xFF8A8D98),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(40.dp)
                                )

                                // Score
                                Text(
                                    text = "${entry.score}",
                                    color = Color(tier.colorHex),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(70.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
