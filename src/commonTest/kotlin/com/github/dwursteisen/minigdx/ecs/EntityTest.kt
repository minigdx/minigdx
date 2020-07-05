package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.test.Test
import kotlin.test.assertTrue

class EntityTest {

    class Example : Component

    @Test
    fun get_it_get_component() {
        val entity = Entity(Engine())
        entity.add(Example())

        assertTrue(entity[Example::class].isNotEmpty())
    }
}
