package com.example.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

object ArenaBackgroundRenderer {

    fun drawWorldEnvironment(
        scope: DrawScope,
        worldId: Int,
        time: Float
    ) {
        val width = scope.size.width
        val height = scope.size.height

        // Base AMOLED deep dark background
        scope.drawRect(
            color = Color(0xFF090A0F),
            size = scope.size
        )

        // Subtle gradient glow from bottom
        val floorGlow = Brush.verticalGradient(
            0.0f to Color.Transparent,
            0.6f to Color.Transparent,
            1.0f to when (worldId) {
                1 -> Color(0x2200E5FF)
                2 -> Color(0x25FF6D00)
                3 -> Color(0x2200E676)
                4 -> Color(0x25D500F9)
                else -> Color(0x28FF1744)
            }
        )
        scope.drawRect(brush = floorGlow, size = scope.size)

        // Coordinate Grid
        drawCoordinateGrid(scope, worldId, time)

        // Ground arena baseline
        val groundY = height * 0.72f
        scope.drawLine(
            color = when (worldId) {
                1 -> Color(0xFF00E5FF)
                2 -> Color(0xFFFF9100)
                3 -> Color(0xFF00E676)
                4 -> Color(0xFFE040FB)
                else -> Color(0xFFFF5252)
            },
            start = Offset(0f, groundY),
            end = Offset(width, groundY),
            strokeWidth = 2.5f
        )

        // Specific world background animations
        when (worldId) {
            1 -> drawNumberPlains(scope, time, groundY)
            2 -> drawAlgebraCity(scope, time, groundY)
            3 -> drawGeometryRealm(scope, time, groundY)
            4 -> drawFunctionSector(scope, time, groundY)
            5 -> drawCalculusCore(scope, time, groundY)
        }
    }

    private fun drawCoordinateGrid(scope: DrawScope, worldId: Int, time: Float) {
        val width = scope.size.width
        val height = scope.size.height
        val gridStep = 45f
        val gridColor = when (worldId) {
            1 -> Color(0x1200E5FF)
            2 -> Color(0x14FF6D00)
            3 -> Color(0x1200E676)
            4 -> Color(0x14D500F9)
            else -> Color(0x18FF1744)
        }

        // Horizontal lines
        var y = 0f
        while (y <= height) {
            scope.drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridStep
        }

        // Vertical lines with subtle scrolling
        val xOffset = (time * 15f) % gridStep
        var x = -xOffset
        while (x <= width) {
            if (x >= 0f) {
                scope.drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
            }
            x += gridStep
        }
    }

    private fun drawNumberPlains(scope: DrawScope, time: Float, groundY: Float) {
        // Floating arithmetic glyphs in background
        val symbols = listOf("+", "−", "×", "÷", "=", "π")
        val width = scope.size.width
        for (i in 0 until 6) {
            val sx = ((i * 70f + (time * 12f * (i + 1))) % (width + 60f)) - 30f
            val sy = (groundY * 0.25f) + (sin(time * 2f + i) * 20f)
            scope.drawCircle(
                color = Color(0x2000E5FF),
                radius = 16f,
                center = Offset(sx, sy)
            )
        }
    }

    private fun drawAlgebraCity(scope: DrawScope, time: Float, groundY: Float) {
        // Neon pillars / variable towers
        val width = scope.size.width
        val towers = listOf(0.15f, 0.35f, 0.65f, 0.85f)
        for ((idx, ratio) in towers.withIndex()) {
            val tx = width * ratio
            val th = 110f + (sin(time * 2f + idx) * 15f)
            scope.drawRect(
                color = Color(0x18FF9100),
                topLeft = Offset(tx - 18f, groundY - th),
                size = androidx.compose.ui.geometry.Size(36f, th)
            )
            scope.drawLine(
                color = Color(0x40FF6D00),
                start = Offset(tx, groundY - th),
                end = Offset(tx, groundY),
                strokeWidth = 2f
            )
        }
    }

    private fun drawGeometryRealm(scope: DrawScope, time: Float, groundY: Float) {
        // Rotating wireframe polygons
        val width = scope.size.width
        val cx = width * 0.5f
        val cy = groundY * 0.35f
        val r = 50f

        val path = Path()
        for (i in 0 until 6) {
            val angle = (time * 0.8f) + (i * Math.PI.toFloat() / 3f)
            val px = cx + cos(angle) * r
            val py = cy + sin(angle) * r
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        scope.drawPath(path, Color(0x3500E676), style = Stroke(width = 2f))
    }

    private fun drawFunctionSector(scope: DrawScope, time: Float, groundY: Float) {
        // Oscillating sine wave graph
        val width = scope.size.width
        val path = Path()
        var started = false
        val waveBaseY = groundY * 0.40f

        var px = 0f
        while (px <= width) {
            val py = waveBaseY + (sin((px * 0.02f) + (time * 3f)) * 30f)
            if (!started) {
                path.moveTo(px, py)
                started = true
            } else {
                path.lineTo(px, py)
            }
            px += 8f
        }
        scope.drawPath(path, Color(0x50D500F9), style = Stroke(width = 2.5f))
    }

    private fun drawCalculusCore(scope: DrawScope, time: Float, groundY: Float) {
        // Infinite spiral / integration vortex
        val cx = scope.size.width * 0.5f
        val cy = groundY * 0.38f

        for (ring in 1..4) {
            val radius = ring * 25f + (sin(time * 3f + ring) * 5f)
            scope.drawCircle(
                color = Color(0x30FF1744),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5f)
            )
        }
    }
}
