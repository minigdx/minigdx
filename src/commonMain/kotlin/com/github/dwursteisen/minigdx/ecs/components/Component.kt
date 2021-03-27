package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.reflect.KClass

interface Component {

    fun onAdded(entity: Entity) = Unit

    fun onRemoved(entity: Entity) = Unit

    fun onComponentUpdated(componentType: KClass<out Component>) = Unit

    fun onDetach(parent: Entity) = Unit

    fun onAttach(parent: Entity) = Unit
}
