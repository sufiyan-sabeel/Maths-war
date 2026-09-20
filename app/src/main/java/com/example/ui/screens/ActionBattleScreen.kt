package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.game.engine.GameEngine
import com.example.game.render.GameCanvasRenderer
import com.example.game.ui.GameHudOverlay
import com.example.game.ui.TouchControlsOverlay

@Composable
fun ActionBattleScreen(
    engine: GameEngine,
    onNavigateBack: () -> Unit
) {
    var animTick by remember { mutableFloatStateOf(0f) }
    var snapshot by remember { mutableStateOf(engine.getSnapshot()) }
    var isPaused by remember { mutableStateOf(engine.isPaused) }
    var showCollisionDebug by remember { mutableStateOf(false) }
    var fpsText by remember { mutableStateOf("60 FPS") }
    var frameCount by remember { mutableStateOf(0) }
    var fpsTimer by remember { mutableFloatStateOf(0f) }

    // 60 FPS Game Loop
    LaunchedEffect(isPaused) {
        var lastFrameTime = System.nanoTime()
        while (true) {
            withFrameNanos { now ->
                val dtNano = now - lastFrameTime
                lastFrameTime = now
                val dt = (dtNano / 1_000_000_000f).coerceIn(0.005f, 0.033f)

                if (!isPaused) {
                    animTick += dt
                    fpsTimer += dt
                    frameCount++
                    if (fpsTimer >= 1.0f) {
                        fpsText = "$frameCount FPS"
                        frameCount = 0
                        fpsTimer = 0f
                    }
                    engine.update(dt)
                    snapshot = engine.getSnapshot()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A0E))
            .testTag("action_battle_screen")
    ) {
        // 1. Hardware-Accelerated Game Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            GameCanvasRenderer.render(
                drawScope = this,
                snapshot = snapshot,
                animTick = animTick,
                showCollisionDebug = showCollisionDebug
            )
        }

        // 2. Touch Controls (Joystick & Action Buttons)
        TouchControlsOverlay(
            touchInput = engine.currentInput,
            specialSymbol = snapshot.player.selectedSpecial.symbol,
            specialReady = snapshot.player.specialEnergy >= snapshot.player.selectedSpecial.energyCost,
            onSpecialClick = {
                engine.currentInput.specialTriggered = true
            }
        )

        // 3. HUD Overlay (Health, Shield, Combo, Waves, Announcements, Menus)
        GameHudOverlay(
            snapshot = snapshot,
            onPauseClick = {
                isPaused = true
                engine.isPaused = true
            },
            onResumeClick = {
                isPaused = false
                engine.isPaused = false
            },
            onRestartClick = {
                engine.resetGame(snapshot.waveIndex)
                isPaused = false
            },
            onQuitClick = {
                isPaused = false
                engine.isPaused = false
                onNavigateBack()
            },
            isPaused = isPaused
        )

        // 4. Development-Only Debug Overlay (Only visible in DEBUG builds)
        if (BuildConfig.DEBUG) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 70.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xE611131A))
                    .border(1.dp, Color(0xFFE67E22), RoundedCornerShape(6.dp))
                    .clickable { showCollisionDebug = !showCollisionDebug }
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "DEBUG OVERLAY ($fpsText)",
                        color = Color(0xFFE67E22),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "STATE: ${snapshot.waveState} | LVL: ${snapshot.levelNumber}",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "POS: (${snapshot.player.pos.x.toInt()}, ${snapshot.player.pos.y.toInt()}) | HP: ${snapshot.player.hp.toInt()}",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "ENEMIES: ${snapshot.enemies.size} | CAM: (${snapshot.camera.pos.x.toInt()}, ${snapshot.camera.pos.y.toInt()})",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "COLLISION BOXES: ${if (showCollisionDebug) "ON" else "OFF"} (TAP TO TOGGLE)",
                        color = if (showCollisionDebug) Color.Green else Color.Gray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
