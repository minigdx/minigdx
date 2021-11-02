package com.github.dwursteisen.minigdx.ecs.components.particles

import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity

/**
 * Particle Emitter is the source of every particles.
 *
 * It will build particles for you.
 */
class ParticleEmitterComponent(
    val particleConfiguration: ParticleConfiguration
) : Component {

    private var owner: Entity? = null

    override fun onAdded(entity: Entity) {
        owner = entity
        if (particleConfiguration.emitOnStartup) {
            emit()
        }
    }

    override fun onRemoved(entity: Entity) {
        owner = null
    }

    /**
     * Emit particles regarding the particle configuration
     */
    fun emit() {
        val o = owner
        if (o != null) {
            if (o.hasComponent(ParticleEmitterActiveComponent::class)) {
                o.get(ParticleEmitterActiveComponent::class).reset()
            } else {
                o.add(ParticleEmitterActiveComponent(particleConfiguration))
            }
        }
    }

    /**
     * Stop the particles emission.
     */
    fun stop() {
        owner?.remove(ParticleEmitterActiveComponent::class)
    }
}
