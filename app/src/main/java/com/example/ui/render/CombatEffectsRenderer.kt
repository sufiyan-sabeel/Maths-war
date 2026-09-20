package com.example.ui.render

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.model.FloatingCombatText
import com.example.model.MathParticle

object CombatEffectsRenderer {

    fun drawCombatEffects(
        scope: DrawScope,
        particles: List<MathParticle>,
        texts: List<FloatingCombatText>
    ) {
        val width = scope.size.width
        val height = scope.size.height

        // Draw Math Particles
        val particlePaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        for (p in particles) {
            val px = p.xRatio * width
            val py = p.yRatio * height
            particlePaint.color = p.color.toArgb()
            particlePaint.alpha = (p.alpha * 255).toInt().coerceIn(0, 255)
            particlePaint.textSize = p.size * 1.5f
            particlePaint.isFakeBoldText = true

            scope.drawContext.canvas.nativeCanvas.drawText(
                p.symbol,
                px,
                py,
                particlePaint
            )
        }

        // Draw Floating Combat Text
        val textPaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        for (t in texts) {
            val tx = t.xRatio * width
            val ty = t.yRatio * height
            textPaint.color = t.color.toArgb()
            textPaint.alpha = (t.alpha * 255).toInt().coerceIn(0, 255)
            textPaint.textSize = t.sizeSp * 2.2f

            scope.drawContext.canvas.nativeCanvas.drawText(
                t.text,
                tx,
                ty,
                textPaint
            )
        }
    }
}
