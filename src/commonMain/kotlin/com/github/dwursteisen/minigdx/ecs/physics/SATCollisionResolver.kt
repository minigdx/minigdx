package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.dot
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class SATCollisionResolver : CollisionResolver {

    override fun collide(entityA: BoundingBox, positionA: Mat4, entityB: BoundingBox, positionB: Mat4): Boolean {
        // Stop collision resolving if the entity can't collide at the moment.
        if (!mightCollide(entityA, positionA, entityB, positionB)) {
            return false
        }

        val axisA = extractAxis(entityA)
            .map { translation(Float3(it.x, it.y, it.z)) }
            .map { positionA * it }
            .map { it.translation }

        val axisB = extractAxis(entityB)
            .map { translation(Float3(it.x, it.y, it.z)) }
            .map { positionB * it }
            .map { it.translation }

        val axis = axisA + axisB
        return axis.all { ax ->
            val a = project(ax, entityA, positionA)
            val b = project(ax, entityB, positionB)
            a.overlap(b)
        }
    }

    private fun toNormal(vertices: List<Vertex>): List<Vector3> {
        return vertices.mapIndexed { index, _ ->
            val next = if (index != vertices.lastIndex) {
                vertices[index + 1]
            } else {
                vertices[0]
            }

            val x = vertices[index].position.x - next.position.x
            val y = vertices[index].position.y - next.position.y
            val z = vertices[index].position.z - next.position.z

            Vector3(x, y, z).normal().normalize()
        }
    }

    private fun extractAxis(box: BoundingBox): List<Vector3> {
        val triangles = box.order.map { box.vertices[it] }
            .chunked(3)

        val normals = triangles.map { toNormal(it) }

        return normals.flatten()
    }

    data class Projection(val min: Float, val max: Float) {
        fun overlap(p2: Projection): Boolean {
            // Check if this projection overlaps with the passed one
            return !(p2.max < min || max < p2.min)
        }
    }

    fun project(
        axis: Float3,
        box: BoundingBox,
        transformation: Mat4
    ): Projection {
        var min = Float.POSITIVE_INFINITY
        var max = Float.NEGATIVE_INFINITY

        box.vertices.forEach { vertex ->
            val vertexPosition = Float3(
                vertex.position.x,
                vertex.position.y,
                vertex.position.z
            )
            val translation = (transformation * translation(vertexPosition)).translation
            val proj = dot(axis, translation)

            min = min(min, proj)
            max = max(max, proj)
        }

        return Projection(min, max)
    }

    fun mightCollide(entityA: BoundingBox, positionA: Mat4, entityB: BoundingBox, positionB: Mat4): Boolean {
        // compute distance between positionA and positionB
        val midline = positionB.position.minus(positionA.position)
        val length = midline.length
        // compute sum of radius
        val sumRadius = entityA.radius + entityB.radius
        // radius > length means collision
        return sumRadius >= length
    }

    private val Float3.length: Float
    get() {
        return sqrt(x * x + y * y + z * z)
    }
}
