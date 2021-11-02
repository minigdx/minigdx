package com.github.dwursteisen.minigdx.ecs

import ModelFactory.gameContext
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EntityTest {

    class Example : Component

    @Test
    fun findAll_it_gets_components() {
        val engine = Engine(gameContext())

        val entity = Entity(engine)
        entity.add(Example())

        engine.update(0f)
        assertTrue(entity.findAll(Example::class).isNotEmpty())
    }

    @Test
    fun get_it_gets_components() {
        val engine = Engine(gameContext())

        val entity = Entity(engine)
        val expected = Example()
        entity.add(expected)

        engine.update(0f)
        assertSame(expected, entity.get(Example::class))
    }

    @Test
    fun get_it_gets_components_before_update() {
        val engine = Engine(gameContext())

        val entity = Entity(engine)
        val expected = Example()
        entity.add(expected)

        assertSame(expected, entity.get(Example::class))
    }
}
