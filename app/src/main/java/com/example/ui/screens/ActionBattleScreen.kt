package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
            GameCanvasRenderer.render(this, snapshot, animTick)
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
    }
}
