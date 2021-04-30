package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class TextEffectSystem : System(EntityQuery.of(TextComponent::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        entity.findAll(TextComponent::class).forEach {
            it.update(delta)
        }
    }
}
