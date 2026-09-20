package com.example.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.StickmanPose
import kotlin.math.cos
import kotlin.math.sin

object StickmanRenderer {

    fun drawStickman(
        scope: DrawScope,
        centerX: Float,
        centerY: Float,
        scale: Float,
        pose: StickmanPose,
        color: Color,
        auraColor: Color,
        isFacingRight: Boolean = true,
        animationTick: Float = 0f,
        isMathRage: Boolean = false,
        weaponSymbol: String = "="
    ) {
        val dir = if (isFacingRight) 1f else -1f
        val s = scale
        val time = animationTick

        // Math Rage ambient aura
        if (isMathRage) {
            scope.drawCircle(
                color = auraColor.copy(alpha = 0.25f + (0.15f * sin(time * 6f))),
                radius = 75f * s,
                center = Offset(centerX, centerY - 25f * s)
            )
            scope.drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = 55f * s,
                center = Offset(centerX, centerY - 25f * s)
            )
        }

        // Draw shadow on ground
        scope.drawOval(
            color = Color.Black.copy(alpha = 0.4f),
            topLeft = Offset(centerX - 35f * s, centerY + 58f * s),
            size = androidx.compose.ui.geometry.Size(70f * s, 16f * s)
        )

        // Joint positions based on pose
        val headYOffset = when (pose) {
            StickmanPose.IDLE -> sin(time * 3f) * 3f * s
            StickmanPose.RUN -> sin(time * 8f) * 4f * s
            StickmanPose.JUMP -> -25f * s
            StickmanPose.DODGE -> 20f * s
            StickmanPose.BLOCK -> 5f * s
            StickmanPose.BASIC_ATTACK_PUNCH -> 2f * s
            StickmanPose.BASIC_ATTACK_KICK -> -8f * s
            StickmanPose.HIT_REACTION -> 10f * s
            StickmanPose.SPECIAL_CHANNEL -> -5f * s
            StickmanPose.SPECIAL_RELEASE -> 0f
            StickmanPose.VICTORY -> -12f * s
            StickmanPose.DEFEAT -> 32f * s
        }

        val baseHead = Offset(centerX + (if (pose == StickmanPose.HIT_REACTION) -15f * dir * s else 0f), centerY - 45f * s + headYOffset)
        val neck = Offset(baseHead.x, baseHead.y + 16f * s)
        val pelvis = Offset(
            baseHead.x + (if (pose == StickmanPose.RUN) -10f * dir * s else 0f),
            centerY + 18f * s + (headYOffset * 0.5f)
        )

        val headRadius = 14f * s
        val strokeWidth = 5.5f * s

        // Draw Head (glowing outline with dark center)
        scope.drawCircle(
            color = Color.Black,
            radius = headRadius,
            center = baseHead
        )
        scope.drawCircle(
            color = if (isMathRage) Color(0xFFFFD600) else color,
            radius = headRadius,
            center = baseHead,
            style = Stroke(width = strokeWidth)
        )

        // Draw Eye / Visor (glowing horizontal slit)
        val eyeX = baseHead.x + (7f * dir * s)
        val eyeY = baseHead.y - 2f * s
        scope.drawLine(
            color = if (isMathRage) Color.White else auraColor,
            start = Offset(eyeX - 4f * s, eyeY),
            end = Offset(eyeX + 5f * dir * s, eyeY),
            strokeWidth = 3f * s,
            cap = StrokeCap.Round
        )

        // Draw Spine
        scope.drawLine(
            color = color,
            start = neck,
            end = pelvis,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Arms and Legs according to Pose
        when (pose) {
            StickmanPose.IDLE -> {
                val breath = sin(time * 3f) * 5f * s
                // Left Arm
                val elbowL = Offset(neck.x - (14f * dir * s), neck.y + 14f * s + breath)
                val handL = Offset(neck.x - (10f * dir * s), neck.y + 30f * s + breath)
                scope.drawLine(color, neck, elbowL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, elbowL, handL, strokeWidth, StrokeCap.Round)

                // Right Arm (front, slightly cocked)
                val elbowR = Offset(neck.x + (16f * dir * s), neck.y + 14f * s - breath)
                val handR = Offset(neck.x + (24f * dir * s), neck.y + 24f * s - breath)
                scope.drawLine(color, neck, elbowR, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, elbowR, handR, strokeWidth, StrokeCap.Round)

                // Left Leg
                val kneeL = Offset(pelvis.x - 12f * s, pelvis.y + 22f * s)
                val footL = Offset(pelvis.x - 16f * s, centerY + 58f * s)
                scope.drawLine(color, pelvis, kneeL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, kneeL, footL, strokeWidth, StrokeCap.Round)

                // Right Leg
                val kneeR = Offset(pelvis.x + 12f * s, pelvis.y + 22f * s)
                val footR = Offset(pelvis.x + 16f * s, centerY + 58f * s)
                scope.drawLine(color, pelvis, kneeR, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, kneeR, footR, strokeWidth, StrokeCap.Round)
            }
            StickmanPose.RUN -> {
                val cycle = sin(time * 10f)
                val cosCycle = cos(time * 10f)

                // Arms pump
                val handL = Offset(neck.x - (25f * cycle * dir * s), neck.y + 20f * s)
                val handR = Offset(neck.x + (25f * cycle * dir * s), neck.y + 20f * s)
                scope.drawLine(color, neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, neck, handR, strokeWidth, StrokeCap.Round)

                // Legs sprint
                val kneeL = Offset(pelvis.x - (20f * cycle * s), pelvis.y + 20f * s)
                val footL = Offset(pelvis.x - (30f * cycle * s), centerY + 55f * s + (cosCycle * 8f * s))
                val kneeR = Offset(pelvis.x + (20f * cycle * s), pelvis.y + 20f * s)
                val footR = Offset(pelvis.x + (30f * cycle * s), centerY + 55f * s - (cosCycle * 8f * s))
                scope.drawLine(color, pelvis, kneeL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, kneeL, footL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, pelvis, kneeR, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, kneeR, footR, strokeWidth, StrokeCap.Round)
            }
            StickmanPose.BASIC_ATTACK_PUNCH -> {
                // Left arm cocked back
                val handL = Offset(neck.x - (18f * dir * s), neck.y + 10f * s)
                scope.drawLine(color, neck, handL, strokeWidth, StrokeCap.Round)

                // Right arm extended forward in massive straight punch
                val elbowR = Offset(neck.x + (25f * dir * s), neck.y - 2f * s)
                val handR = Offset(neck.x + (52f * dir * s), neck.y - 2f * s)
                scope.drawLine(color, neck, elbowR, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, elbowR, handR, strokeWidth, StrokeCap.Round)

                // Punch energy spark at fist
                scope.drawCircle(
                    color = auraColor,
                    radius = 8f * s,
                    center = handR
                )

                // Legs in deep lunge
                val footBack = Offset(pelvis.x - (28f * dir * s), centerY + 58f * s)
                val footFront = Offset(pelvis.x + (24f * dir * s), centerY + 58f * s)
                scope.drawLine(color, pelvis, footBack, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, pelvis, footFront, strokeWidth, StrokeCap.Round)
            }
            StickmanPose.BASIC_ATTACK_KICK -> {
                // Aerial kick
                val footBack = Offset(pelvis.x - (18f * dir * s), centerY + 58f * s)
                scope.drawLine(color, pelvis, footBack, strokeWidth, StrokeCap.Round)

                val kneeKick = Offset(pelvis.x + (24f * dir * s), pelvis.y - 5f * s)
                val footKick = Offset(pelvis.x + (54f * dir * s), pelvis.y - 12f * s)
                scope.drawLine(color, pelvis, kneeKick, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, kneeKick, footKick, strokeWidth, StrokeCap.Round)

                // Kick arc
                scope.drawCircle(
                    color = Color.White,
                    radius = 9f * s,
                    center = footKick
                )

                // Guarding arms
                val handL = Offset(neck.x - (10f * dir * s), neck.y + 15f * s)
                val handR = Offset(neck.x + (15f * dir * s), neck.y + 12f * s)
                scope.drawLine(color, neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, neck, handR, strokeWidth, StrokeCap.Round)
            }
            StickmanPose.BLOCK -> {
                // Crossed arms forming block
                val handL = Offset(neck.x + (12f * dir * s), neck.y - 5f * s)
                val handR = Offset(neck.x + (14f * dir * s), neck.y + 5f * s)
                scope.drawLine(color, neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, neck, handR, strokeWidth, StrokeCap.Round)

                // Mathematical absolute value barrier [  |  ]
                val barrierX = neck.x + (25f * dir * s)
                scope.drawLine(
                    color = Color(0xFF00E5FF),
                    start = Offset(barrierX, centerY - 45f * s),
                    end = Offset(barrierX, centerY + 55f * s),
                    strokeWidth = 6f * s,
                    cap = StrokeCap.Square
                )
                // Legs planted firmly
                scope.drawLine(color, pelvis, Offset(pelvis.x - 22f * s, centerY + 58f * s), strokeWidth, StrokeCap.Round)
                scope.drawLine(color, pelvis, Offset(pelvis.x + 22f * s, centerY + 58f * s), strokeWidth, StrokeCap.Round)
            }
            StickmanPose.HIT_REACTION -> {
                // Recoiling back, arms flailing
                val handL = Offset(neck.x - (25f * dir * s), neck.y - 15f * s)
                val handR = Offset(neck.x - (10f * dir * s), neck.y - 20f * s)
                scope.drawLine(Color(0xFFFF5252), neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(Color(0xFFFF5252), neck, handR, strokeWidth, StrokeCap.Round)

                val footL = Offset(pelvis.x - (25f * dir * s), centerY + 58f * s)
                val footR = Offset(pelvis.x + (5f * dir * s), centerY + 58f * s)
                scope.drawLine(Color(0xFFFF5252), pelvis, footL, strokeWidth, StrokeCap.Round)
                scope.drawLine(Color(0xFFFF5252), pelvis, footR, strokeWidth, StrokeCap.Round)
            }
            StickmanPose.SPECIAL_RELEASE -> {
                // Thrusting both hands forward firing equation blast
                val handL = Offset(neck.x + (42f * dir * s), neck.y - 5f * s)
                val handR = Offset(neck.x + (44f * dir * s), neck.y + 5f * s)
                scope.drawLine(color, neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, neck, handR, strokeWidth, StrokeCap.Round)

                // Powerful horizontal beam of mathematical light
                val beamOrigin = Offset(neck.x + (45f * dir * s), neck.y)
                val beamEnd = Offset(beamOrigin.x + (300f * dir * s), neck.y)
                scope.drawLine(
                    color = Color.White,
                    start = beamOrigin,
                    end = beamEnd,
                    strokeWidth = 14f * s,
                    cap = StrokeCap.Round
                )
                scope.drawLine(
                    color = auraColor,
                    start = beamOrigin,
                    end = beamEnd,
                    strokeWidth = 24f * s,
                    cap = StrokeCap.Round
                )

                // Legs deep stance
                scope.drawLine(color, pelvis, Offset(pelvis.x - (30f * dir * s), centerY + 58f * s), strokeWidth, StrokeCap.Round)
                scope.drawLine(color, pelvis, Offset(pelvis.x + (25f * dir * s), centerY + 58f * s), strokeWidth, StrokeCap.Round)
            }
            StickmanPose.VICTORY -> {
                // Arms raised high
                val handL = Offset(neck.x - 22f * s, neck.y - 30f * s)
                val handR = Offset(neck.x + 22f * s, neck.y - 30f * s)
                scope.drawLine(color, neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, neck, handR, strokeWidth, StrokeCap.Round)

                // Standing straight
                scope.drawLine(color, pelvis, Offset(pelvis.x - 14f * s, centerY + 58f * s), strokeWidth, StrokeCap.Round)
                scope.drawLine(color, pelvis, Offset(pelvis.x + 14f * s, centerY + 58f * s), strokeWidth, StrokeCap.Round)

                // Halo / Q.E.D. crown
                scope.drawCircle(
                    color = Color(0xFFFFD600),
                    radius = 20f * s,
                    center = Offset(baseHead.x, baseHead.y - 12f * s),
                    style = Stroke(width = 2.5f * s)
                )
            }
            StickmanPose.DEFEAT -> {
                // Collapsed on knees
                val kneeL = Offset(pelvis.x - 10f * s, centerY + 50f * s)
                val footL = Offset(pelvis.x - 25f * s, centerY + 58f * s)
                val kneeR = Offset(pelvis.x + 15f * s, centerY + 50f * s)
                val footR = Offset(pelvis.x + 28f * s, centerY + 58f * s)
                scope.drawLine(color.copy(alpha = 0.6f), pelvis, kneeL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color.copy(alpha = 0.6f), kneeL, footL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color.copy(alpha = 0.6f), pelvis, kneeR, strokeWidth, StrokeCap.Round)
                scope.drawLine(color.copy(alpha = 0.6f), kneeR, footR, strokeWidth, StrokeCap.Round)

                // Head lowered, arms hanging
                val handL = Offset(neck.x - 10f * s, centerY + 45f * s)
                val handR = Offset(neck.x + 10f * s, centerY + 45f * s)
                scope.drawLine(color.copy(alpha = 0.6f), neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color.copy(alpha = 0.6f), neck, handR, strokeWidth, StrokeCap.Round)
            }
            else -> {
                // Default / Dodge / Jump
                val handL = Offset(neck.x - 16f * dir * s, neck.y + 12f * s)
                val handR = Offset(neck.x + 16f * dir * s, neck.y + 12f * s)
                scope.drawLine(color, neck, handL, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, neck, handR, strokeWidth, StrokeCap.Round)
                scope.drawLine(color, pelvis, Offset(pelvis.x - 14f * s, centerY + 58f * s), strokeWidth, StrokeCap.Round)
                scope.drawLine(color, pelvis, Offset(pelvis.x + 14f * s, centerY + 58f * s), strokeWidth, StrokeCap.Round)
            }
        }
    }
}
