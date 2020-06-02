package com.github.dwursteisen.minigdx.ecs

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EntityQueryTest {

    class Name : Component

    @Test
    fun `accept | it should accept included component`() {
        val entity = Engine().create {
            add(Name())
        }
        val query = EntityQuery(Name::class)

        assertTrue(query.accept(entity))
    }

    @Test
    fun `accept | it should not accept excluded component`() {
        val entity = Engine().create {
            add(Name())
        }
        val query = EntityQuery(include = emptyList(), exclude = listOf(Name::class))

        assertFalse(query.accept(entity))
    }

    @Test
    fun `accept | it should not accept not included component`() {
        val entity = Engine().create {}
        val query = EntityQuery(Name::class)

        assertFalse(query.accept(entity))
    }
}
