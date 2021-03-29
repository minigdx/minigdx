package com.github.dwursteisen.minigdx.ecs.physics

import MockPlatformContext
import com.curiouscreature.kotlin.math.Float3
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Resolution
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.physics.SATCollisionResolver.Axis
import createGameConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.github.dwursteisen.minigdx.ecs.components.assertEquals as assertEqualsFloat

class SATCollisionResolverTest {

    private val collider = SATCollisionResolver()

    private val engine = Engine(
        GameContext(
            MockPlatformContext(createGameConfiguration()),
            Resolution(100, 100)
        )
    )

    private fun createEntities(): Pair<Entity, Entity> {
        val a = engine.create {
            add(BoundingBox.default())
            add(Position())
        }
        val b = engine.create {
            add(BoundingBox.default())
            add(Position())
        }
        return a to b
    }

    @Test
    fun collide_it_does_not_collide() {
        val (a, b) = createEntities()
        b.position.addGlobalTranslation(x = 5f)
        val result = collider.collide(a, b)
        assertFalse(result)
    }

    @Test
    fun collide_it_collides() {
        val (a, b) = createEntities()
        b.position.addGlobalTranslation(x = 0.5f)
        val result = collider.collide(a, b)
        assertTrue(result)
    }

    @Test
    fun mightCollide_it_mights_collide() {
        val (a, b) = createEntities()
        a.position.addGlobalTranslation(x = 1f)
        b.position.addGlobalTranslation(x = 1.5f)
        val result = collider.collide(a, b)
        assertTrue(result)
    }

    @Test
    fun mightCollide_it_does_not_mights_collide() {
        val (a, b) = createEntities()
        a.position.addGlobalTranslation(x = 1f)
        b.position.addGlobalTranslation(x = 4f)
        val result = collider.collide(a, b)

        assertFalse(result)
    }

    @Test
    fun axis_it_extracts_axis_of_triangles() {
        val axis = SATCollisionResolver.Triangle(
            a = Float3(0f, 0f, 0f),
            b = Float3(1f, 0f, 0f),
            c = Float3(0f, 1f, 0f)
        ).axis

        val (a, b, c) = axis
        assertEquals(Axis(0f, 0f, 1f), a)
        assertEquals(0f, b.x)
        assertEqualsFloat(-0.70710677f, b.y)
        assertEqualsFloat(-0.70710677f, b.z)
        assertEquals(Axis(0f, 1f, -0f), c)
    }
}
