package com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard

import androidx.compose.ui.geometry.Offset
import java.util.UUID


enum class EffectType {
    HEART,
    STAR,
    CONFETTI,
    EMOJI_CUSTOM
}
data class AnimationEvent(
    val id: String = UUID.randomUUID().toString(),
    val x: Float,
    val y: Float,
    val particleCount: Int = 1, // 1, 2, or 3
    val effectType: EffectType = EffectType.HEART,
    val timestamp: Long = System.currentTimeMillis()
)

data class ParticleState(
    val id: String,
    val emoji: String,
    val startPosition: Offset,
    val initialVelocity: Offset,      // pixels per second
    val initialRotation: Float,        // degrees
    val rotationSpeed: Float,          // degrees per second
    val scale: Float = 1f,
    val createdAt: Long = System.currentTimeMillis()
)

// Physics constants
object ParticlePhysics {
    const val GRAVITY = 800f           // pixels per second²
    const val AIR_RESISTANCE = 0.98f   // velocity multiplier per frame
    const val INITIAL_VELOCITY_Y = -600f  // upward velocity
    const val INITIAL_VELOCITY_X_RANGE = 150f  // horizontal spread
    const val ROTATION_SPEED_RANGE = 180f  // max rotation per second
    const val ANIMATION_DURATION_MS = 2000L
    const val SCALE_MIN = 0.8f
    const val SCALE_MAX = 1.2f
}