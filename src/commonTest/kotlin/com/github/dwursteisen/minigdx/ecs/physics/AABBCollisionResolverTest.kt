package com.github.dwursteisen.minigdx.ecs.physics

import MockPlatformContext
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Resolution
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.BoundingBoxComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import createGameConfiguration
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AABBCollisionResolverTest {

    private val collider = AABBCollisionResolver()

    private val engine = Engine(
        GameContext(
            MockPlatformContext(createGameConfiguration()),
            Resolution(100, 100)
        )
    )

    @Test
    fun collide_it_does_not_collide() {
        val a = engine.create {
            add(BoundingBoxComponent.default())
            add(Position())
        }
        val b = engine.create {
            add(BoundingBoxComponent.default())
            add(Position().addGlobalTranslation(x = 100))
        }
        val result = collider.collide(
            a,
            b
        )
        assertFalse(result)
    }

    @Test
    fun collide_it_collides() {
        val a = engine.create {
            add(BoundingBoxComponent.default())
            add(Position())
        }
        val b = engine.create {
            add(BoundingBoxComponent.default())
            add(Position().addGlobalTranslation(x = 0.5f))
        }
        val result = collider.collide(
            a,
            b
        )

        assertTrue(result)
    }
}
