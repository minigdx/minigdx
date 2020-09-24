package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.ecs.components.Position
import kotlin.test.Test
import kotlin.test.assertEquals

class MeshPrimitiveRenderStageTest {

    @Test
    fun sort_backToFront_it_sorts() {
        val camera = Position()
        val toSort: MutableList<Pair<Position, *>> = mutableListOf(
            Position().setGlobalTranslation(z = 20f) to "front",
            Position().setGlobalTranslation(z = 50f) to "back"
        )
        backToFront(camera, toSort)

        val (backA, frontA) = toSort
        assertEquals("back", backA.second)
        assertEquals("front", frontA.second)

        camera.setGlobalTranslation(z = 51f)

        backToFront(camera, toSort)

        val (backB, frontB) = toSort
        assertEquals("front", backB.second)
        assertEquals("back", frontB.second)

        frontB.first.setGlobalTranslation(z = 300f)

        backToFront(camera, toSort)

        val (backC, frontC) = toSort
        assertEquals("back", backC.second)
        assertEquals("front", frontC.second)
    }

    fun backToFront(camera: Position, elements: MutableList<Pair<Position, *>>) {
        elements.sortByDescending { (position, _) ->
            camera.translation.dist2(position.translation)
        }
    }
}
