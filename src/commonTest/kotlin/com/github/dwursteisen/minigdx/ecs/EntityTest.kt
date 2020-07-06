package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EntityTest {

    class Example : Component

    @Test
    fun findAll_it_gets_components() {
        val entity = Entity(Engine())
        entity.add(Example())

        assertTrue(entity.findAll(Example::class).isNotEmpty())
    }

    @Test
    fun get_it_gets_components() {
        val entity = Entity(Engine())
        val expected = Example()
        entity.add(expected)

        assertSame(expected, entity.get(Example::class))
    }
}
