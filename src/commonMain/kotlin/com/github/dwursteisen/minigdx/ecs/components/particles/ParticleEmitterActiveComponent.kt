package com.github.dwursteisen.minigdx.ecs.components.particles

import com.github.dwursteisen.minigdx.ecs.components.Component

/**
 * Mark a particle emitter as active.
 * It will emit particles as long as this component is on the entity.
 */
internal class ParticleEmitterActiveComponent(
    configuration: ParticleConfiguration,
) : Component {

    val base: ParticleConfiguration = configuration
    var current: MutableParticleConfiguration = configuration.toMutable()
        private set

    fun reset() {
        current = base.toMutable()
    }
}
