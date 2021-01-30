package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import createGlContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EntityQueryTest {

    class Name : Component

    @Test
    fun accept__it_should_accept_included_component() {
        val entity = Engine(GameContext(createGlContext())).create {
            add(Name())
        }
        val query = EntityQuery(Name::class)

        assertTrue(query.accept(entity))
    }

    @Test
    fun accept__it_should_not_accept_excluded_component() {
        val entity = Engine(GameContext(createGlContext())).create {
            add(Name())
        }
        val query = EntityQuery(
            include = emptyList(),
            exclude = listOf(Name::class)
        )

        assertFalse(query.accept(entity))
    }

    @Test
    fun accept__it_should_not_accept_not_included_component() {
        val entity = Engine(GameContext(createGlContext())).create {}
        val query = EntityQuery(Name::class)

        assertFalse(query.accept(entity))
    }
}
