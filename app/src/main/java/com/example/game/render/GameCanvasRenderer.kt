package com.example.game.render

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.game.engine.GameStateSnapshot
import com.example.game.model.EnemyType
import com.example.game.model.ImpactEffect
import com.example.game.model.MathEntity
import com.example.game.model.MathParticle
import com.example.game.model.Projectile
import kotlin.math.sin

object GameCanvasRenderer {

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 36f
        color = android.graphics.Color.WHITE
        typeface = android.graphics.Typeface.MONOSPACE
        isFakeBoldText = true
    }

    private val symbolPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 44f
        color = android.graphics.Color.WHITE
        typeface = android.graphics.Typeface.MONOSPACE
        isFakeBoldText = true
    }

    private val bgEqPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
        textSize = 26f
        color = android.graphics.Color.argb(28, 140, 145, 155)
        typeface = android.graphics.Typeface.MONOSPACE
    }

    fun render(
        drawScope: DrawScope,
        snapshot: GameStateSnapshot,
        animTick: Float
    ) {
        val width = drawScope.size.width
        val height = drawScope.size.height

        // 1. Fill deep charcoal/black background (No neon/cyberpunk)
        drawScope.drawRect(
            color = Color(0xFF0D0E12),
            size = Size(width, height)
        )

        // Camera calculations
        val cam = snapshot.camera
        val centerX = width / 2f
        val centerY = height / 2f
        val zoom = cam.zoom

        drawScope.translate(centerX, centerY) {
            drawScope.scale(zoom, zoom, Offset.Zero) {
                drawScope.translate(-cam.pos.x, -cam.pos.y) {

                    // 2. Render Mathematical Arena
                    drawMathematicalArena(drawScope, animTick)

                    // 3. Render Enemies (Soldiers or Monsters)
                    for (enemy in snapshot.enemies) {
                        if (enemy.isSoldier) {
                            StickmanActionRenderer.drawSoldierStickman(drawScope, enemy, animTick)
                        } else {
                            drawMathEntity(drawScope, enemy, animTick)
                        }
                    }

                    // 4. Render Player Stickman
                    StickmanActionRenderer.drawStickman(drawScope, snapshot.player, animTick)

                    // 5. Render Projectiles
                    for (proj in snapshot.projectiles) {
                        drawProjectile(drawScope, proj)
                    }

                    // 6. Render Impact Rings & Impact Frames
                    for (imp in snapshot.impacts) {
                        drawImpact(drawScope, imp)
                    }

                    // 7. Render Particles & Floating Damage Numbers
                    for (p in snapshot.particles) {
                        drawParticle(drawScope, p)
                    }
                }
            }
        }
    }

    private fun drawMathematicalArena(drawScope: DrawScope, animTick: Float) {
        val groundY = 720f
        val left = -400f
        val right = 2200f

        // Subtle Mathematical Grid
        val gridStep = 80f
        for (x in -400..2200 step 80) {
            drawScope.drawLine(
                color = Color(0x0CFFFFFF),
                start = Offset(x.toFloat(), -200f),
                end = Offset(x.toFloat(), 1000f),
                strokeWidth = 1f
            )
        }
        for (y in -200..1000 step 80) {
            drawScope.drawLine(
                color = Color(0x0CFFFFFF),
                start = Offset(left, y.toFloat()),
                end = Offset(right, y.toFloat()),
                strokeWidth = 1f
            )
        }

        // Animated Function Graph Waveform in subtle earth-tone amber
        val wavePath = Path()
        var first = true
        for (x in -200..1800 step 20) {
            val waveY = groundY - 260f + sin(x * 0.008f - animTick * 2.5f) * 45f
            if (first) {
                wavePath.moveTo(x.toFloat(), waveY)
                first = false
            } else {
                wavePath.lineTo(x.toFloat(), waveY)
            }
        }
        drawScope.drawPath(
            path = wavePath,
            color = Color(0x18E67E22),
            style = Stroke(width = 2.0f)
        )

        // Mathematical background equations
        val native = drawScope.drawContext.canvas.nativeCanvas
        native.drawText("f(x) = ∑ [A_n · sin(nωt)]", 200f, 320f, bgEqPaint)
        native.drawText("e^(iπ) + 1 = 0", 800f, 250f, bgEqPaint)
        native.drawText("lim_{Δx→0} [f(x+Δx) - f(x)] / Δx", 1200f, 360f, bgEqPaint)
        native.drawText("∇ × B = μ₀J + μ₀ε₀(∂E/∂t)", 450f, 520f, bgEqPaint)

        // Ground Line (Clean graphite axis)
        drawScope.drawLine(
            color = Color(0xFF6B7280),
            start = Offset(left, groundY),
            end = Offset(right, groundY),
            strokeWidth = 3f,
            cap = StrokeCap.Square
        )
        // Sub-ground shadow
        drawScope.drawRect(
            color = Color(0xFF090A0E),
            topLeft = Offset(left, groundY),
            size = Size(right - left, 400f)
        )

        // Ground Coordinate Ticks with small earth-tone accent
        val tickTextPaint = Paint().apply {
            color = android.graphics.Color.argb(80, 156, 163, 175)
            textSize = 13f
            typeface = android.graphics.Typeface.MONOSPACE
        }
        for (x in -200..1800 step 100) {
            drawScope.drawLine(
                color = Color(0x88E67E22),
                start = Offset(x.toFloat(), groundY - 6f),
                end = Offset(x.toFloat(), groundY + 6f),
                strokeWidth = 1.5f
            )
            native.drawText(
                "x=$x",
                x.toFloat() - 18f,
                groundY + 24f,
                tickTextPaint
            )
        }
    }

    private fun drawMathEntity(drawScope: DrawScope, enemy: MathEntity, animTick: Float) {
        val ex = enemy.pos.x
        val ey = enemy.pos.y
        val native = drawScope.drawContext.canvas.nativeCanvas
        val flashColor = if (enemy.hitFlashTimer > 0f) Color.White else enemy.color

        // Boss Aura (Clean minimal geometric ring)
        if (enemy.isBoss) {
            val auraPulse = 75f * enemy.scale + sin(animTick * 5f) * 8f
            drawScope.drawCircle(
                color = enemy.color.copy(alpha = 0.12f),
                radius = auraPulse,
                center = Offset(ex, ey - enemy.height * 0.5f)
            )
            drawScope.drawCircle(
                color = enemy.color.copy(alpha = 0.45f),
                radius = auraPulse,
                center = Offset(ex, ey - enemy.height * 0.5f),
                style = Stroke(width = 2f)
            )
        }

        // Entity Body Geometry or Typographic Symbol
        when (enemy.type) {
            EnemyType.MONSTER_GEOMETRY_DELTA -> {
                // Equilateral triangle
                val triPath = Path()
                val r = 32f * enemy.scale
                drawScope.rotate(enemy.rotation, Offset(ex, ey - enemy.height * 0.5f)) {
                    triPath.moveTo(ex, ey - enemy.height * 0.5f - r)
                    triPath.lineTo(ex + r * 0.866f, ey - enemy.height * 0.5f + r * 0.5f)
                    triPath.lineTo(ex - r * 0.866f, ey - enemy.height * 0.5f + r * 0.5f)
                    triPath.close()
                    drawScope.drawPath(triPath, flashColor, style = Stroke(width = 3.5f))
                    symbolPaint.color = flashColor.toArgb()
                    symbolPaint.textSize = 28f * enemy.scale
                    native.drawText("Δ", ex, ey - enemy.height * 0.5f + 10f, symbolPaint)
                }
            }
            EnemyType.MONSTER_GEOMETRY_CIRCLE -> {
                val r = 30f * enemy.scale
                drawScope.drawCircle(
                    color = flashColor,
                    radius = r,
                    center = Offset(ex, ey - enemy.height * 0.5f),
                    style = Stroke(width = 3f)
                )
                drawScope.drawCircle(
                    color = flashColor.copy(alpha = 0.4f),
                    radius = r * 0.6f,
                    center = Offset(ex, ey - enemy.height * 0.5f),
                    style = Stroke(width = 1.5f)
                )
                symbolPaint.color = flashColor.toArgb()
                symbolPaint.textSize = 26f * enemy.scale
                native.drawText("2πr", ex, ey - enemy.height * 0.5f + 8f, symbolPaint)
            }
            EnemyType.MONSTER_FRACTION -> {
                drawScope.drawLine(
                    color = flashColor,
                    start = Offset(ex - 24f * enemy.scale, ey - enemy.height * 0.5f),
                    end = Offset(ex + 24f * enemy.scale, ey - enemy.height * 0.5f),
                    strokeWidth = 3f
                )
                symbolPaint.color = flashColor.toArgb()
                symbolPaint.textSize = 22f * enemy.scale
                native.drawText("a", ex, ey - enemy.height * 0.5f - 8f, symbolPaint)
                native.drawText("b", ex, ey - enemy.height * 0.5f + 24f, symbolPaint)
            }
            else -> {
                val symbol = enemy.symbolString.ifEmpty {
                    when (enemy.type) {
                        EnemyType.NUM_0 -> "0"
                        EnemyType.NUM_1 -> "1"
                        EnemyType.NUM_2 -> "2"
                        EnemyType.NUM_7 -> "7"
                        EnemyType.NUM_8 -> "8"
                        EnemyType.NUM_9 -> "9"
                        EnemyType.OP_PLUS -> "+"
                        EnemyType.OP_MINUS -> "−"
                        EnemyType.OP_MULTIPLY -> "×"
                        EnemyType.OP_DIVIDE -> "÷"
                        EnemyType.OP_EQUALS -> "="
                        EnemyType.OP_ROOT -> "√"
                        EnemyType.OP_PERCENT -> "%"
                        EnemyType.MONSTER_ALGEBRA_X -> "x"
                        EnemyType.MONSTER_EQUATION -> "f(x)"
                        else -> "x"
                    }
                }

                drawScope.rotate(enemy.rotation, Offset(ex, ey - enemy.height * 0.5f)) {
                    symbolPaint.color = flashColor.toArgb()
                    symbolPaint.textSize = (44f * enemy.scale)
                    native.drawText(
                        symbol,
                        ex,
                        ey - enemy.height * 0.5f + (14f * enemy.scale),
                        symbolPaint
                    )
                }
            }
        }

        // Shield Ring (if any)
        if (enemy.shield > 0f) {
            val shieldRadius = (enemy.width * 0.7f * enemy.scale)
            drawScope.drawCircle(
                color = Color(0xFF8A8D98).copy(alpha = 0.5f),
                radius = shieldRadius,
                center = Offset(ex, ey - enemy.height * 0.5f),
                style = Stroke(width = 2.5f)
            )
        }

        // Mini Health Bar above Entity
        val barWidth = enemy.width * 1.2f * enemy.scale
        val barHeight = 5f
        val barX = ex - barWidth / 2f
        val barY = ey - enemy.height * enemy.scale - 16f
        val hpPct = (enemy.hp / enemy.maxHp).coerceIn(0f, 1f)

        drawScope.drawRect(
            color = Color(0x66000000),
            topLeft = Offset(barX, barY),
            size = Size(barWidth, barHeight)
        )
        drawScope.drawRect(
            color = if (enemy.isBoss) Color(0xFFC0392B) else flashColor,
            topLeft = Offset(barX, barY),
            size = Size(barWidth * hpPct, barHeight)
        )
    }

    private fun drawProjectile(drawScope: DrawScope, proj: Projectile) {
        val native = drawScope.drawContext.canvas.nativeCanvas
        drawScope.rotate(proj.rotation, Offset(proj.pos.x, proj.pos.y)) {
            val paint = Paint().apply {
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                textSize = proj.size
                color = proj.color.toArgb()
                typeface = android.graphics.Typeface.MONOSPACE
                isFakeBoldText = true
            }
            native.drawText(proj.symbol, proj.pos.x, proj.pos.y + proj.size * 0.35f, paint)
        }
    }

    private fun drawImpact(drawScope: DrawScope, imp: ImpactEffect) {
        val cx = imp.pos.x
        val cy = imp.pos.y
        val alpha = imp.alpha

        when (imp.type) {
            "IMPACT_FRAME" -> {
                // High-energy 4-point impact star / diamond flash
                val rad = imp.radius * 0.8f
                val starPath = Path()
                starPath.moveTo(cx, cy - rad)
                starPath.quadraticTo(cx + rad * 0.15f, cy - rad * 0.15f, cx + rad, cy)
                starPath.quadraticTo(cx + rad * 0.15f, cy + rad * 0.15f, cx, cy + rad)
                starPath.quadraticTo(cx - rad * 0.15f, cy + rad * 0.15f, cx - rad, cy)
                starPath.quadraticTo(cx - rad * 0.15f, cy - rad * 0.15f, cx, cy - rad)
                starPath.close()

                drawScope.drawPath(
                    path = starPath,
                    color = imp.color.copy(alpha = alpha * 0.85f)
                )
            }
            "DUST_PUFF" -> {
                // Soft earthy dust cloud cluster
                val dustColor = Color(0xFF6B7280).copy(alpha = alpha * 0.45f)
                drawScope.drawCircle(dustColor, radius = imp.radius * 0.5f, center = Offset(cx - 15f, cy))
                drawScope.drawCircle(dustColor, radius = imp.radius * 0.7f, center = Offset(cx, cy - 6f))
                drawScope.drawCircle(dustColor, radius = imp.radius * 0.5f, center = Offset(cx + 15f, cy))
            }
            else -> {
                // Clean shockwave ring
                drawScope.drawCircle(
                    color = imp.color.copy(alpha = alpha),
                    radius = imp.radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.5f)
                )
            }
        }
    }

    private fun drawParticle(drawScope: DrawScope, p: MathParticle) {
        val native = drawScope.drawContext.canvas.nativeCanvas
        drawScope.rotate(p.rotation, Offset(p.pos.x, p.pos.y)) {
            val paint = Paint().apply {
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                textSize = p.size
                color = p.color.copy(alpha = p.alpha).toArgb()
                typeface = android.graphics.Typeface.MONOSPACE
                isFakeBoldText = true
            }
            native.drawText(p.text, p.pos.x, p.pos.y, paint)
        }
    }
}
