package com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard



import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun HeartParticle(
    particle: ParticleState,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val centerOffset = with(density) { 15.dp.toPx() }
    // Time-based animation
    var animationTime by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(true) }

    // Animation using frame-based updates
    LaunchedEffect(particle.id) {
        val startTime = withFrameNanos { it }

        while (isAnimating) {
            withFrameNanos { frameTime ->
                val elapsedNanos = frameTime - startTime
                val elapsedSeconds = elapsedNanos / 1_000_000_000f
                animationTime = elapsedSeconds

                // End animation after duration
                if (elapsedNanos > ParticlePhysics.ANIMATION_DURATION_MS * 1_000_000L) {
                    isAnimating = false
                }
            }
        }
        onAnimationEnd()
    }

    // Calculate physics-based position
    val currentPosition = remember(animationTime) {
        calculatePosition(
            startPosition = particle.startPosition,
            initialVelocity = particle.initialVelocity,
            time = animationTime
        )
    }

    // Calculate rotation
    val currentRotation = remember(animationTime) {
        particle.initialRotation + (particle.rotationSpeed * animationTime)
    }

    // Calculate alpha (fade out in last 30%)
    val alpha = remember(animationTime) {
        val progress = animationTime / (ParticlePhysics.ANIMATION_DURATION_MS / 1000f)
        when {
            progress < 0.7f -> 1f
            else -> 1f - ((progress - 0.7f) / 0.3f)
        }.coerceIn(0f, 1f)
    }

    // Calculate scale (pulse effect)
    val scale = remember(animationTime) {
        val progress = animationTime / (ParticlePhysics.ANIMATION_DURATION_MS / 1000f)
        // Start big, shrink slightly, then normal
        when {
            progress < 0.1f -> particle.scale * (1f + (1f - progress / 0.1f) * 0.3f)
            progress < 0.3f -> particle.scale * (1f + ((progress - 0.1f) / 0.2f) * 0.1f)
            else -> particle.scale
        }
    }

    if (alpha > 0) {
        Box(
            modifier = modifier
                .offset {
                    IntOffset(
                        x = (currentPosition.x - centerOffset).toInt(),
                        y = (currentPosition.y - centerOffset).toInt()
                    )
                }
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                    rotationZ = currentRotation
                    // Add slight 3D rotation for realism
                    rotationY = currentRotation * 0.3f
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = particle.emoji,
                fontSize = 28.sp,
                lineHeight = 28.sp

            )
        }
    }
}

/**
 * Calculate position using physics equations:
 * x = x0 + vx * t * airResistance^(t*60)
 * y = y0 + vy * t + 0.5 * g * t²
 */
private fun calculatePosition(
    startPosition: androidx.compose.ui.geometry.Offset,
    initialVelocity: Offset,
    time: Float
): Offset {
    // Apply air resistance to horizontal movement (exponential decay)
    val airResistanceFactor = ParticlePhysics.AIR_RESISTANCE.pow(time * 60)

    // Horizontal: x = x0 + vx * t * airResistance
    val x = startPosition.x + initialVelocity.x * time * airResistanceFactor

    // Vertical: y = y0 + vy * t + 0.5 * g * t²
    // Apply lighter air resistance to vertical for more natural fall
    val y = startPosition.y +
            (initialVelocity.y * time * airResistanceFactor.pow(0.5f)) +
            (0.5f * ParticlePhysics.GRAVITY * time * time)

    return androidx.compose.ui.geometry.Offset(x, y)
}
@Composable
fun AnimationOverlay(
    animationEvents: SharedFlow<AnimationEvent>,
    modifier: Modifier = Modifier
) {
    // Active particles
    val activeParticles = remember { mutableStateMapOf<String, ParticleState>() }

    // Maximum concurrent particles (performance)
    val maxParticles = 30

    // Collect animation events
    LaunchedEffect(Unit) {
        animationEvents.collect { event ->
            // Generate particles based on count
            val newParticles = generateParticles(event)

            // Add particles (with limit)
            newParticles.forEach { particle ->
                if (activeParticles.size < maxParticles) {
                    activeParticles[particle.id] = particle
                } else {
                    // Remove oldest particle
                    val oldest = activeParticles.values.minByOrNull { it.createdAt }
                    oldest?.let { activeParticles.remove(it.id) }
                    activeParticles[particle.id] = particle
                }
            }
        }
    }

    // Render particles
    Box(modifier = modifier.fillMaxSize()) {
        activeParticles.forEach { (id, particle) ->
            key(id) {
                HeartParticle(
                    particle = particle,
                    onAnimationEnd = {
                        activeParticles.remove(id)
                    }
                )
            }
        }
    }
}

/**
 * Generate particles with different spread patterns based on count:
 * - 1 particle: Slight right bias
 * - 2 particles: Spread left and right
 * - 3 particles: Fan pattern (left, center, right)
 */
private fun generateParticles(event: AnimationEvent): List<ParticleState> {
    val emoji = when (event.effectType) {
        EffectType.HEART -> listOf("❤️", "💖", "💕", "💗", "💓").random()
        EffectType.STAR -> listOf("⭐", "✨", "🌟", "💫").random()
        EffectType.CONFETTI -> listOf("🎊", "🎉", "✨").random()
        EffectType.EMOJI_CUSTOM -> "😊"
    }

    val count = event.particleCount.coerceIn(1, 3)
    val startPosition = Offset(event.x, event.y)

    return when (count) {
        1 -> listOf(generateSingleParticle(startPosition, emoji))
        2 -> generateTwoParticles(startPosition, emoji)
        3 -> generateThreeParticles(startPosition, emoji)
        else -> listOf(generateSingleParticle(startPosition, emoji))
    }
}

/**
 * Single particle: Slight random offset, bias toward right
 */
private fun generateSingleParticle(
    startPosition: Offset,
    emoji: String
): ParticleState {
    // Random angle between -10° and 30° (slight right bias)
    val angleDegrees = Random.nextFloat() * 40f - 10f
    val angleRadians = angleDegrees * PI.toFloat() / 180f

    // Calculate velocity components
    val speed = ParticlePhysics.INITIAL_VELOCITY_Y * Random.nextFloat().let { 0.9f + it * 0.2f }
    val vx = -speed * sin(angleRadians) * 0.4f
    val vy = speed * cos(angleRadians)

    return ParticleState(
        id = UUID.randomUUID().toString(),
        emoji = emoji,
        startPosition = startPosition,
        initialVelocity = Offset(vx, vy),
        initialRotation = Random.nextFloat() * 30f - 15f,
        rotationSpeed = (Random.nextFloat() * 2f - 1f) * ParticlePhysics.ROTATION_SPEED_RANGE,
        scale = ParticlePhysics.SCALE_MIN + Random.nextFloat() *
                (ParticlePhysics.SCALE_MAX - ParticlePhysics.SCALE_MIN)
    )
}

/**
 * Two particles: Spread symmetrically left and right
 */
private fun generateTwoParticles(
    startPosition: Offset,
    emoji: String
): List<ParticleState> {
    // Angles: -25° to -35° (left) and 25° to 35° (right)
    val leftAngle = -(25f + Random.nextFloat() * 10f)
    val rightAngle = 25f + Random.nextFloat() * 10f

    return listOf(
        createParticleAtAngle(startPosition, emoji, leftAngle, rotationBias = -1f),
        createParticleAtAngle(startPosition, emoji, rightAngle, rotationBias = 1f)
    )
}

/**
 * Three particles: Fan pattern - left, center, right
 */
private fun generateThreeParticles(
    startPosition: Offset,
    emoji: String
): List<ParticleState> {
    // Angles: -35° to -45° (left), -5° to 5° (center), 35° to 45° (right)
    val leftAngle = -(35f + Random.nextFloat() * 10f)
    val centerAngle = Random.nextFloat() * 10f - 5f
    val rightAngle = 35f + Random.nextFloat() * 10f

    return listOf(
        createParticleAtAngle(startPosition, emoji, leftAngle, rotationBias = -1f, scaleMultiplier = 0.9f),
        createParticleAtAngle(startPosition, emoji, centerAngle, rotationBias = 0f, scaleMultiplier = 1.1f),
        createParticleAtAngle(startPosition, emoji, rightAngle, rotationBias = 1f, scaleMultiplier = 0.9f)
    )
}

/**
 * Create a particle at specific angle
 * @param angleDegrees Angle from vertical (negative = left, positive = right)
 * @param rotationBias Direction bias for rotation (-1 = CCW, 0 = random, 1 = CW)
 */
private fun createParticleAtAngle(
    startPosition: Offset,
    emoji: String,
    angleDegrees: Float,
    rotationBias: Float = 0f,
    scaleMultiplier: Float = 1f
): ParticleState {
    val angleRadians = angleDegrees * PI.toFloat() / 180f

    // Add some randomness to speed
    val speedMultiplier = 0.85f + Random.nextFloat() * 0.3f
    val speed = ParticlePhysics.INITIAL_VELOCITY_Y * speedMultiplier

    // Calculate velocity (vy is negative for upward, vx based on angle)
    val vx = -speed * sin(angleRadians) * 0.5f
    val vy = speed * cos(angleRadians)

    // Calculate rotation speed with bias
    val baseRotationSpeed = ParticlePhysics.ROTATION_SPEED_RANGE * 0.5f
    val rotationSpeed = when {
        rotationBias < 0 -> -(baseRotationSpeed + Random.nextFloat() * baseRotationSpeed)
        rotationBias > 0 -> baseRotationSpeed + Random.nextFloat() * baseRotationSpeed
        else -> (Random.nextFloat() * 2f - 1f) * ParticlePhysics.ROTATION_SPEED_RANGE
    }

    // Scale with some randomness
    val baseScale = ParticlePhysics.SCALE_MIN +
            Random.nextFloat() * (ParticlePhysics.SCALE_MAX - ParticlePhysics.SCALE_MIN)

    return ParticleState(
        id = UUID.randomUUID().toString(),
        emoji = emoji,
        startPosition = startPosition,
        initialVelocity = Offset(vx, vy),
        initialRotation = angleDegrees * 0.5f + Random.nextFloat() * 20f - 10f,
        rotationSpeed = rotationSpeed,
        scale = baseScale * scaleMultiplier
    )
}