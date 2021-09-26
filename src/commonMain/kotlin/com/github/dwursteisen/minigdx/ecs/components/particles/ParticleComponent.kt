package com.github.dwursteisen.minigdx.ecs.components.particles

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.math.Vector3

/**
 * Particle.
 *
 * The entity needs to have a position.
 */
class ParticleComponent(
    val configuration: ParticleConfiguration,
    var ttl: Seconds = 1f,
    var direction: Vector3 = Vector3(),
    var rotation: Vector3 = Vector3(),
    var scale: Vector3 = Vector3(1f, 1f, 1f),
) : Component
