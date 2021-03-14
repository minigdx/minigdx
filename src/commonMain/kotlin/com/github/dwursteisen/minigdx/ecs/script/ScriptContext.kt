package com.github.dwursteisen.minigdx.ecs.script

import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.math.abs
import kotlin.math.max

interface ScriptContext {

    /**
     * Time elapsed since the last update
     */
    val delta: Seconds

    /**
     * Release the script context and continue at the same place
     * at the next render loop.
     */
    suspend fun yield()
}

/**
 * Wait the [duration] seconds before passing to the next step.
 */
suspend fun ScriptContext.waiting(duration: Seconds) {
    var waitTime = 0f
    while (waitTime < duration) {
        waitTime += delta
        yield()
    }
}

/**
 * Move of [x] [y] [z] units in total and spend [duration] seconds to do it.
 */
suspend fun ScriptContext.moveOf(
    entity: Entity,
    x: Coordinate = 0f,
    y: Coordinate = 0f,
    z: Coordinate = 0f,
    duration: Seconds = 1f
) {
    fun secureDiv(value: Float): Float {
        return if (value.isInfinite()) {
            0f
        } else {
            value
        }
    }
    val speedX = secureDiv(duration / x.toFloat())
    val speedY = secureDiv(duration / y.toFloat())
    val speedZ = secureDiv(duration / z.toFloat())

    var xCounter = abs(x.toFloat())
    var yCounter = abs(y.toFloat())
    var zCounter = abs(z.toFloat())
    while (xCounter > 0f || yCounter > 0f || zCounter > 0f) {
        xCounter = max(0f, xCounter - abs(speedX) * delta)
        yCounter = max(0f, yCounter - abs(speedY) * delta)
        zCounter = max(0f, zCounter - abs(speedZ) * delta)
        entity.get(Position::class).addLocalTranslation(x = speedX, y = speedY, z = speedZ, delta = delta)
        yield()
    }
}
