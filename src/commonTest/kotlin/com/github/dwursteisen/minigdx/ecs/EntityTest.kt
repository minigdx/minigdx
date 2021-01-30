package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import createGlContext
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EntityTest {

    class Example : Component

    @Test
    fun findAll_it_gets_components() {
        val entity = Entity(Engine(GameContext(createGlContext())))
        entity.add(Example())

        assertTrue(entity.findAll(Example::class).isNotEmpty())
    }

    @Test
    fun get_it_gets_components() {
        val entity = Entity(Engine(GameContext(createGlContext())))
        val expected = Example()
        entity.add(expected)

        assertSame(expected, entity.get(Example::class))
    }
}
