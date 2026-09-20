package com.example.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.TouchInput
import com.example.game.model.Vec2
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun TouchControlsOverlay(
    touchInput: TouchInput,
    specialSymbol: String,
    specialReady: Boolean,
    onSpecialClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // LEFT: Virtual Movement Joystick
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 32.dp)
        ) {
            VirtualJoystick(
                onMove = { dx, dy ->
                    touchInput.joystickMove.x = dx
                    touchInput.joystickMove.y = dy
                },
                onRelease = {
                    touchInput.joystickMove.x = 0f
                    touchInput.joystickMove.y = 0f
                }
            )
        }

        // RIGHT: Arcade Action Buttons
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 28.dp)
        ) {
            ActionButtonsCluster(
                touchInput = touchInput,
                specialSymbol = specialSymbol,
                specialReady = specialReady,
                onSpecialClick = onSpecialClick
            )
        }
    }
}

@Composable
private fun VirtualJoystick(
    onMove: (Float, Float) -> Unit,
    onRelease: () -> Unit
) {
    val baseRadius = 65f
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color(0x221E2028))
            .border(1.5.dp, Color(0x406B7280), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val delta = offset - center
                        val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                        val clampedDist = minOf(dist, baseRadius)
                        val norm = if (dist > 0) delta / dist else Offset.Zero
                        knobOffset = norm * clampedDist
                        onMove(norm.x, norm.y)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = knobOffset + dragAmount
                        val dist = sqrt(newOffset.x * newOffset.x + newOffset.y * newOffset.y)
                        val clampedDist = minOf(dist, baseRadius)
                        val norm = if (dist > 0) newOffset / dist else Offset.Zero
                        knobOffset = norm * clampedDist
                        onMove(norm.x, norm.y)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onRelease()
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onRelease()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Center Knob
        Box(
            modifier = Modifier
                .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xDD2A2D37))
                .border(2.dp, Color(0xFFE67E22), CircleShape)
        )
    }
}

@Composable
private fun ActionButtonsCluster(
    touchInput: TouchInput,
    specialSymbol: String,
    specialReady: Boolean,
    onSpecialClick: () -> Unit
) {
    Box(modifier = Modifier.size(240.dp)) {

        // BLOCK Button (Top Left of cluster)
        ArcadeRoundButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag("btn_block"),
            size = 52.dp,
            color = Color(0xFF374151),
            borderColor = Color(0xFF6B7280),
            label = "BLOCK",
            fontSize = 10.sp,
            onPressStart = { touchInput.blockHeld = true },
            onPressEnd = { touchInput.blockHeld = false }
        )

        // SPECIAL Button (Top End)
        ArcadeRoundButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-6).dp)
                .testTag("btn_special"),
            size = 60.dp,
            color = if (specialReady) Color(0xFF2D261E) else Color(0xFF1E2028),
            borderColor = if (specialReady) Color(0xFFE67E22) else Color(0xFF4B5563),
            label = specialSymbol,
            fontSize = 16.sp,
            onPressStart = {
                touchInput.specialTriggered = true
                onSpecialClick()
            }
        )

        // DASH Button (Middle Left)
        ArcadeRoundButton(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = 12.dp)
                .testTag("btn_dash"),
            size = 56.dp,
            color = Color(0xFF262833),
            borderColor = Color(0xFF6B7280),
            label = "DASH",
            fontSize = 11.sp,
            onPressStart = { touchInput.dashTriggered = true }
        )

        // JUMP Button (Bottom Center-Left)
        ArcadeRoundButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = (-30).dp)
                .testTag("btn_jump"),
            size = 64.dp,
            color = Color(0xFF262833),
            borderColor = Color(0xFF8A8D98),
            label = "JUMP",
            fontSize = 12.sp,
            onPressStart = { touchInput.jumpTriggered = true }
        )

        // ATTACK Button (Bottom Right - Largest, Orange Accent)
        ArcadeRoundButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .testTag("btn_attack"),
            size = 76.dp,
            color = Color(0xFFE67E22),
            borderColor = Color(0xFFF39C12),
            label = "ATK",
            fontSize = 16.sp,
            onPressStart = { touchInput.attackTriggered = true }
        )
    }
}

@Composable
private fun ArcadeRoundButton(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp,
    color: Color,
    borderColor: Color = color,
    label: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    onPressStart: () -> Unit = {},
    onPressEnd: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isPressed) color.copy(alpha = 0.95f) else color.copy(alpha = 0.6f))
            .border(2.dp, if (isPressed) Color.White else borderColor, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressStart()
                        tryAwaitRelease()
                        isPressed = false
                        onPressEnd()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color(0xFFF0F0F5),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize
        )
    }
}
