package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.particles.ParticleComponent
import com.github.dwursteisen.minigdx.ecs.components.particles.ParticleEmitterActiveComponent
import com.github.dwursteisen.minigdx.ecs.components.particles.ParticleEmitterComponent
import com.github.dwursteisen.minigdx.ecs.components.particles.ParticleGeneration
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position

class ParticlesEmitterSystem : System(
    EntityQuery.of(
        ParticleEmitterComponent::class,
        ParticleEmitterActiveComponent::class
    )
) {

    override fun update(delta: Seconds, entity: Entity) {
        val active = entity.get(ParticleEmitterActiveComponent::class)

        active.current.duration -= delta
        if (active.current.duration < 0f) {
            when (active.current.time) {
                -1L -> active.reset()
                0L -> entity.remove(ParticleEmitterActiveComponent::class)
                else -> active.current.time--
            }
        }
        val progress = active.current.duration / active.base.duration
        val particles = active.base.emitter(entity, progress)

        val generation = ParticleGeneration(entity).apply {
            this.emmiterProgress = progress
            this.particlesTotal = particles
        }
        (0 until particles).forEach {
            val creationProgress = it / (particles - 1).toFloat()
            generation.particlesIndex = it
            generation.particlesIndexPercent = creationProgress

            val component = ParticleComponent(configuration = active.base)

            val particle = active.base.factory(entity, generation)
            active.base.particle(component, generation)
            particle.position.setGlobalTranslation(entity.position.translation)
            particle.add(component)
        }
    }
}
