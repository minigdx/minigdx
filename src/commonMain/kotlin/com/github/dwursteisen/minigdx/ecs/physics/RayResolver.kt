package com.github.dwursteisen.minigdx.ecs.physics

import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.math.Vector3

class RayResolver {

    // copy from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Intersector.java#L514
    fun intersectRayBounds(origin: Vector3, direction: Vector3, bounding: Entity): Vector3? {
        val box = bounding.get(BoundingBox::class)

        if (box.contains(origin)) {
            return origin.copy()
        }
        var lowest = 0f
        var t: Float
        var hit = false

        // min x
        if (origin.x <= box.min.x && direction.x > 0) {
            t = (box.min.x - origin.x) / direction.x
            if (t >= 0) {
                val v2 = direction.copy().scale(t).add(origin)
                if (v2.y >= box.min.y && v2.y <= box.max.y && v2.z >= box.min.z && v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true
                    lowest = t
                }
            }
        }
        // max x
        if (origin.x >= box.max.x && direction.x < 0) {
            t = (box.max.x - origin.x) / direction.x
            if (t >= 0) {
                val v2 = direction.copy().scale(t).add(origin)
                if (v2.y >= box.min.y && v2.y <= box.max.y && v2.z >= box.min.z && v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true
                    lowest = t
                }
            }
        }
        // min y
        if (origin.y <= box.min.y && direction.y > 0) {
            t = (box.min.y - origin.y) / direction.y
            if (t >= 0) {
                val v2 = direction.copy().scale(t).add(origin)
                if (v2.x >= box.min.x && v2.x <= box.max.x && v2.z >= box.min.z && v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true
                    lowest = t
                }
            }
        }
        // max y
        if (origin.y >= box.max.y && direction.y < 0) {
            t = (box.max.y - origin.y) / direction.y
            if (t >= 0) {
                val v2 = direction.copy().scale(t).add(origin)
                if (v2.x >= box.min.x && v2.x <= box.max.x && v2.z >= box.min.z && v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true
                    lowest = t
                }
            }
        }
        // min z
        if (origin.z <= box.min.z && direction.z > 0) {
            t = (box.min.z - origin.z) / direction.z
            if (t >= 0) {
                val v2 = direction.copy().scale(t).add(origin)
                if (v2.x >= box.min.x && v2.x <= box.max.x && v2.y >= box.min.y && v2.y <= box.max.y && (!hit || t < lowest)) {
                    hit = true
                    lowest = t
                }
            }
        }
        // max z
        if (origin.z >= box.max.z && direction.z < 0) {
            t = (box.max.z - origin.z) / direction.z
            if (t >= 0) {
                val v2 = direction.copy().scale(t).add(origin)
                if (v2.x >= box.min.x && v2.x <= box.max.x && v2.y >= box.min.y && v2.y <= box.max.y && (!hit || t < lowest)) {
                    hit = true
                    lowest = t
                }
            }
        }
        return if (hit) {
            val intersection = direction.copy().scale(lowest).add(origin)
            if (intersection.x < box.min.x) {
                intersection.x = box.min.x
            } else if (intersection.x > box.max.x) {
                intersection.x = box.max.x
            }
            if (intersection.y < box.min.y) {
                intersection.y = box.min.y
            } else if (intersection.y > box.max.y) {
                intersection.y = box.max.y
            }
            if (intersection.z < box.min.z) {
                intersection.z = box.min.z
            } else if (intersection.z > box.max.z) {
                intersection.z = box.max.z
            }
            intersection
        } else {
            null
        }
    }
}
