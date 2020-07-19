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

class SATCollisionResolver : CollisionResolver {

    override fun collide(entityA: BoundingBox, positionA: Mat4, entityB: BoundingBox, positionB: Mat4): Boolean {
        val axisA = extractAxis(entityA)
            .map { translation(Float3(it.x, it.y, it.z)) }
            .map { it * positionA }
            .map { it.translation }

        val axisB = extractAxis(entityB)
            .map { translation(Float3(it.x, it.y, it.z)) }
            .map { it * positionA }
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
        var minX = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY

        box.vertices.forEach { vertex ->
            val vertexPosition = Float3(
                vertex.position.x,
                vertex.position.y,
                vertex.position.z
            )
            val translation = (translation(vertexPosition) * transformation).translation
            val proj = dot(axis, translation)

            minX = min(minX, proj)
            maxX = max(maxX, proj)
        }

        return Projection(minX, maxX)
    }
}
