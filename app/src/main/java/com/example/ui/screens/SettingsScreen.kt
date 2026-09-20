package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioManager

@Composable
fun SettingsScreen(
    audioManager: AudioManager,
    onToggleSound: () -> Unit,
    onToggleBgm: () -> Unit,
    onToggleHaptics: () -> Unit,
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
                .padding(horizontal = 16.dp, vertical = 16.dp)
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
                    modifier = Modifier.testTag("settings_back_btn")
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
                        text = "SETTINGS & SYSTEM",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Audio, feedback, and mathematical engine configuration",
                        color = Color(0xFF90A4AE),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AUDIO & HAPTICS",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF13141F)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingToggleRow(
                        title = "Sound Effects",
                        subtitle = "Procedural combat SFX and hits",
                        icon = Icons.Default.VolumeUp,
                        isChecked = audioManager.soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        testTag = "sound_toggle"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingToggleRow(
                        title = "Synth BGM",
                        subtitle = "Real-time rhythmic arpeggiator",
                        icon = Icons.Default.MusicNote,
                        isChecked = audioManager.bgmEnabled,
                        onCheckedChange = { onToggleBgm() },
                        testTag = "bgm_toggle"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingToggleRow(
                        title = "Tactile Haptics",
                        subtitle = "Device vibrations on combos and impacts",
                        icon = Icons.Default.Vibration,
                        isChecked = audioManager.hapticsEnabled,
                        onCheckedChange = { onToggleHaptics() },
                        testTag = "haptics_toggle"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ABOUT MATH//BRAWL",
                color = Color(0xFFFF9100),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF13141F)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MATH//BRAWL",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "MATH//BRAWL is an original 2D mathematical stickman action game built natively for Android in Kotlin and Jetpack Compose. Combats are entirely governed by real-time mathematical reasoning and formulas.",
                        color = Color(0xFFB0B0C0),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Original procedural sound synthesis engine\n• Zero third-party proprietary assets\n• 100% offline local Room persistence\n• Respects AMOLED displays with true black UI",
                        color = Color(0xFF8C8D9E),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00E5FF),
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF757585),
                    fontSize = 11.sp
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color(0xFF00E5FF),
                uncheckedThumbColor = Color(0xFF757585),
                uncheckedTrackColor = Color(0xFF1E2030)
            )
        )
    }
}
