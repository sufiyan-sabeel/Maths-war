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
        animTick: Float,
        showCollisionDebug: Boolean = false
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

                    // 2. Render Mathematical Arena based on ArenaType
                    drawMathematicalArena(drawScope, snapshot.arenaType, animTick)

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

                    // 8. Debug Collision Outlines
                    if (showCollisionDebug) {
                        val playerHurt = snapshot.player.hurtbox
                        drawScope.drawRect(
                            color = Color.Green,
                            topLeft = Offset(playerHurt.left, playerHurt.top),
                            size = Size(playerHurt.width, playerHurt.height),
                            style = Stroke(width = 2f)
                        )

                        val playerAtk = snapshot.player.attackHitbox
                        if (playerAtk != null) {
                            drawScope.drawRect(
                                color = Color(0xFFFF9800),
                                topLeft = Offset(playerAtk.left, playerAtk.top),
                                size = Size(playerAtk.width, playerAtk.height),
                                style = Stroke(width = 2f)
                            )
                        }

                        for (enemy in snapshot.enemies) {
                            if (!enemy.isDead) {
                                val eHurt = enemy.hurtbox
                                drawScope.drawRect(
                                    color = Color.Red,
                                    topLeft = Offset(eHurt.left, eHurt.top),
                                    size = Size(eHurt.width, eHurt.height),
                                    style = Stroke(width = 2f)
                                )
                                val eAtk = enemy.attackHitbox
                                if (eAtk != null) {
                                    drawScope.drawRect(
                                        color = Color.Yellow,
                                        topLeft = Offset(eAtk.left, eAtk.top),
                                        size = Size(eAtk.width, eAtk.height),
                                        style = Stroke(width = 2f)
                                    )
                                }
                            }
                        }

                        for (proj in snapshot.projectiles) {
                            val pHit = proj.hitbox
                            drawScope.drawRect(
                                color = Color.Cyan,
                                topLeft = Offset(pHit.left, pHit.top),
                                size = Size(pHit.width, pHit.height),
                                style = Stroke(width = 1.5f)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun drawMathematicalArena(
        drawScope: DrawScope,
        arenaType: com.example.game.model.ArenaType,
        animTick: Float
    ) {
        val groundY = 720f
        val left = -400f
        val right = 2200f
        val native = drawScope.drawContext.canvas.nativeCanvas

        // Base Grid
        val gridStep = 80f
        for (x in -400..2200 step 80) {
            drawScope.drawLine(
                color = Color(0x0AFFFFFF),
                start = Offset(x.toFloat(), -200f),
                end = Offset(x.toFloat(), 1000f),
                strokeWidth = 1f
            )
        }
        for (y in -200..1000 step 80) {
            drawScope.drawLine(
                color = Color(0x0AFFFFFF),
                start = Offset(left, y.toFloat()),
                end = Offset(right, y.toFloat()),
                strokeWidth = 1f
            )
        }

        // Arena-Specific Architectural Features
        when (arenaType) {
            com.example.game.model.ArenaType.NUMBER_LAB -> {
                // Cartesian numbers and coordinate pulses
                native.drawText("f(x) = ∑ aₙ · 10ⁿ", 200f, 260f, bgEqPaint)
                native.drawText("ℕ = {0, 1, 2, 3, ...} ⊂ ℤ", 900f, 320f, bgEqPaint)
                native.drawText("GCD(a, b) · LCM(a, b) = |a·b|", 1300f, 420f, bgEqPaint)
            }
            com.example.game.model.ArenaType.EQUATION_FACTORY -> {
                // Mechanical pipeline gears and formula conduits
                native.drawText("W = ∫ F · ds   //   P = dW/dt", 180f, 240f, bgEqPaint)
                native.drawText("E² = (pc)² + (m₀c²)²", 850f, 300f, bgEqPaint)
                native.drawText("∂ρ/∂t + ∇ · (ρv) = 0", 1350f, 400f, bgEqPaint)
                // Industrial pipeline line
                drawScope.drawLine(
                    color = Color(0x1AD35400),
                    start = Offset(left, 480f),
                    end = Offset(right, 480f),
                    strokeWidth = 4f
                )
            }
            com.example.game.model.ArenaType.GEOMETRY_RUINS -> {
                // Euclidean pillars and golden ratio spiral
                native.drawText("a² + b² = c²   //   φ = (1 + √5)/2 ≈ 1.618", 220f, 260f, bgEqPaint)
                native.drawText("A = ½ab·sin(C)   //   V = ⁴⁄₃πr³", 920f, 340f, bgEqPaint)
                // Golden spiral arc
                val spiralPath = Path()
                spiralPath.moveTo(600f, 400f)
                spiralPath.cubicTo(680f, 350f, 750f, 460f, 850f, 380f)
                drawScope.drawPath(spiralPath, Color(0x18D4A373), style = Stroke(width = 2f))
            }
            com.example.game.model.ArenaType.ALGEBRA_CITY -> {
                // Matrix monoliths and determinant formulas
                native.drawText("det(A - λI) = 0   //   Ax = b", 240f, 250f, bgEqPaint)
                native.drawText("x = [-b ± √(b² - 4ac)] / 2a", 880f, 330f, bgEqPaint)
                native.drawText("rank(A) + nullity(A) = n", 1380f, 440f, bgEqPaint)
            }
            com.example.game.model.ArenaType.INFINITE_GRID -> {
                // Pure Cartesian coordinate axes
                native.drawText("ℝ² = (-∞, +∞) × (-∞, +∞)", 280f, 240f, bgEqPaint)
                native.drawText("x² + y² = r²   //   (x-h)² + (y-k)² = r²", 960f, 320f, bgEqPaint)
                // Center origin indicator
                drawScope.drawCircle(
                    color = Color(0x22E67E22),
                    radius = 120f,
                    center = Offset(800f, 500f),
                    style = Stroke(width = 1.5f)
                )
            }
            com.example.game.model.ArenaType.FUNCTION_CHAMBER -> {
                // Dual sine and cosine waveforms
                val cosPath = Path()
                var firstCos = true
                for (x in -200..1800 step 20) {
                    val waveY = groundY - 320f + kotlin.math.cos(x * 0.009f + animTick * 2.0f) * 50f
                    if (firstCos) {
                        cosPath.moveTo(x.toFloat(), waveY)
                        firstCos = false
                    } else {
                        cosPath.lineTo(x.toFloat(), waveY)
                    }
                }
                drawScope.drawPath(cosPath, Color(0x14CCD1D9), style = Stroke(width = 2f))
                native.drawText("f(x) = A·sin(ωt + φ) + B·cos(ωt)", 200f, 230f, bgEqPaint)
                native.drawText("tan(θ) = sin(θ) / cos(θ)", 920f, 310f, bgEqPaint)
            }
            com.example.game.model.ArenaType.GRAVITY_ARENA -> {
                // Curved spacetime warping grid dipping towards center
                val curvePath = Path()
                curvePath.moveTo(left, 500f)
                curvePath.quadraticTo(800f, 620f, right, 500f)
                drawScope.drawPath(curvePath, Color(0x20D4A373), style = Stroke(width = 2.5f))
                native.drawText("G_μν + Λg_μν = (8πG/c⁴) T_μν", 300f, 250f, bgEqPaint)
                native.drawText("g = GM / r²   //   v_esc = √(2GM/r)", 950f, 330f, bgEqPaint)
            }
            com.example.game.model.ArenaType.FINAL_EQUATION_CORE -> {
                // Cosmic Singularity: rotating concentric formula rings
                val coreX = 800f
                val coreY = 400f
                val ringPulse = 180f + sin(animTick * 3f) * 15f
                drawScope.drawCircle(
                    color = Color(0x15E67E22),
                    radius = ringPulse,
                    center = Offset(coreX, coreY)
                )
                drawScope.drawCircle(
                    color = Color(0x2AE67E22),
                    radius = ringPulse * 0.7f,
                    center = Offset(coreX, coreY),
                    style = Stroke(width = 2f)
                )
                native.drawText("e^(iπ) + 1 = 0", 250f, 220f, bgEqPaint)
                native.drawText("∫_{-∞}^{+∞} e^(-x²) dx = √π", 880f, 280f, bgEqPaint)
                native.drawText("∑_{n=1}^{∞} (1/n²) = π²/6", 1300f, 380f, bgEqPaint)
            }
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
