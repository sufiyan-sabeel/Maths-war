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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.components.ArcadeButton

@Composable
fun ProfileScreen(
    profile: UserProfile,
    onSignOut: () -> Unit,
    onOpenAuth: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rankTier = RankTier.fromRank(profile.rank)
    val isGuest = profile.uid.startsWith("guest_") || profile.uid == "local_guest"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(8.dp))
                    .testTag("profile_btn_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFF0F0F5)
                )
            }

            Text(
                text = "WARRIOR DOSSIER // STATS & RANK",
                color = Color(0xFF8A8D98),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        // Horizontal Landscape Split View
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Panel: Identity & Competitive Rank Card
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Identity Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(rankTier.colorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.username.take(2).uppercase(),
                            color = Color(0xFF0D0E12),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column {
                        Text(
                            text = profile.username,
                            color = Color(0xFFF0F0F5),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (isGuest) "GUEST ACCOUNT (UNSYNCED)" else "FIREBASE SYNCHRONIZED",
                            color = if (isGuest) Color(0xFFE67E22) else Color(0xFF27AE60),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Competitive Rank Tier Badge
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x25E67E22))
                        .border(1.dp, Color(rankTier.colorHex), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "COMPETITIVE STANDING",
                        color = Color(0xFFB0B3BC),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "#${profile.rank}",
                        color = Color(rankTier.colorHex),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = rankTier.title,
                        color = Color(0xFFF0F0F5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (profile.xp % 500) / 500f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(rankTier.colorHex),
                        trackColor = Color(0xFF1C1E26)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NEXT RANK LEVEL: ${500 - (profile.xp % 500)} XP NEEDED",
                        color = Color(0xFF8A8D98),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Action Buttons (Login / Sign Out)
                if (isGuest) {
                    ArcadeButton(
                        text = "LINK / SIGN IN",
                        onClick = onOpenAuth,
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = Color(0xFFE67E22),
                        testTag = "profile_btn_link_auth"
                    )
                } else {
                    ArcadeButton(
                        text = "SIGN OUT",
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = Color(0xFFC0392B),
                        icon = Icons.AutoMirrored.Filled.Logout,
                        testTag = "profile_btn_signout"
                    )
                }
            }

            // Right Panel: Combat Statistics & Unlocked Martial Arts
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD014161E))
                    .border(1.dp, Color(0x356B7280), RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "CAREER BATTLE METRICS",
                    color = Color(0xFF8A8D98),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // 2x2 Metric Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBox(
                        title = "TOTAL SCORE",
                        value = "${profile.totalScore}",
                        color = Color(0xFFE67E22),
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "BEST STAGE SCORE",
                        value = "${profile.bestScore}",
                        color = Color(0xFF3498DB),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBox(
                        title = "LEVELS CLEARED",
                        value = "${profile.completedLevels} / 52",
                        color = Color(0xFF2ECC71),
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "TOTAL STARS",
                        value = "${profile.stars} ★",
                        color = Color(0xFFF1C40F),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "MATHEMATICAL COMBAT POWERS",
                    color = Color(0xFF8A8D98),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // Martial Power Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PowerBadge(title = "+ / − Dash", desc = "Kinetic Burst", unlocked = true, modifier = Modifier.weight(1f))
                    PowerBadge(title = "× / ÷ Whirl", desc = "Rotary Blade", unlocked = profile.level >= 2, modifier = Modifier.weight(1f))
                    PowerBadge(title = "√ Ground Hook", desc = "Subterranean", unlocked = profile.level >= 5, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0D0E12))
            .padding(10.dp)
    ) {
        Text(text = title, color = Color(0xFF8A8D98), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun PowerBadge(
    title: String,
    desc: String,
    unlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (unlocked) Color(0x2527AE60) else Color(0x15374151))
            .border(1.dp, if (unlocked) Color(0x5527AE60) else Color(0x256B7280), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (unlocked) Icons.Default.CheckCircle else Icons.Default.Bolt,
                contentDescription = null,
                tint = if (unlocked) Color(0xFF27AE60) else Color(0xFF8A8D98),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                color = if (unlocked) Color(0xFFF0F0F5) else Color(0xFF8A8D98),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = desc,
            color = Color(0xFF8A8D98),
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
