package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.particles.ParticleComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class ParticlesUpdateSystem : System(EntityQuery.of(ParticleComponent::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        val particleComponent = entity.get(ParticleComponent::class)
        particleComponent.ttl -= delta
        if (particleComponent.ttl < 0f) {
            entity.destroy()
            return
        }

        val configuration = particleComponent.configuration
        configuration.update(delta, entity)
    }
}
