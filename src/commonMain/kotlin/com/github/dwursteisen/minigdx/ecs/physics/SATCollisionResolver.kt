package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.ecs.components.BoundingBoxComponent
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.math.toVector3
import kotlin.math.max
import kotlin.math.min

// https://github.com/CasuallyCritical/OLC_PGEX_Collision3d/blob/eceaf621f1cfccd8bfe954e0b940a89857c6169f/olcPGEX_Collisions3D.h
class SATCollisionResolver : CollisionResolver {

    override fun collide(boxA: BoundingBoxComponent, boxB: BoundingBoxComponent): Boolean {
        // Stop collision resolving if the entity can't collide at the moment.
        if (!mightCollide(boxA, boxB)) {
            return false
        }

        // Create all axes for the two boxes
        val axes = createAxes(boxA) + createAxes(boxB)

        // Look for a projections on the same axe that doesn't overlapping
        val allOverlaps = axes.all { axe ->
            val projectionA = project(axe, boxA)
            val projectionB = project(axe, boxB)
            projectionA.overlap(projectionB)
        }

        // If a projection doesn't overlap another projection,
        // it means that a gap exist between the two shapes.
        // If there is no gap, the two boxes are colliding.
        // If there is a gap, the two boxes are not colliding.
        return allOverlaps
    }

    private fun project(axe: Vector3, boxA: BoundingBoxComponent): Projection {
        var min = Float.POSITIVE_INFINITY
        var max = Float.NEGATIVE_INFINITY
        boxA.edges.forEach {
            val minA = axe.dot(it)
            val maxA = axe.dot(it)
            min = min(min, minA)
            max = max(max, maxA)
        }
        return Projection(min, max)
    }

    /**
     * Create default axes and move then regarding the current box transformation.
     */
    private fun createAxes(boundingBox: BoundingBoxComponent): List<Vector3> {
        val axeX = Vector3(1f, 0f, 0f)
        val axeY = Vector3(0f, 1f, 0f)
        val axeZ = Vector3(0f, 0f, 1f)

        val x = rotation(boundingBox.combinedTransformation) * translation(axeX.toFloat3())
        val y = rotation(boundingBox.combinedTransformation) * translation(axeY.toFloat3())
        val z = rotation(boundingBox.combinedTransformation) * translation(axeZ.toFloat3())

        return listOf(x.translation.toVector3(), y.translation.toVector3(), z.translation.toVector3())
    }

    /**
     * Check if the two box are close enough it might worth to check that a collision can occur.
     *
     * If not, the two boxes are too far from each other: they can not collide.
     */
    private fun mightCollide(entityA: BoundingBoxComponent, entityB: BoundingBoxComponent): Boolean {
        // compute distance between positionA and positionB
        val midline = entityB.center.mutable().sub(entityA.center)
        val length = midline.length()
        // compute sum of radius
        val sumRadius = entityA.radius + entityB.radius
        // radius > length means the two entities are close enought so a collision can occur.
        return sumRadius >= length
    }

    private data class Projection(val min: Float, val max: Float) {

        fun overlap(p2: Projection): Boolean {
            // Check if this projection overlaps with the passed one
            return !(p2.max < min || max < p2.min)
        }
    }
}
