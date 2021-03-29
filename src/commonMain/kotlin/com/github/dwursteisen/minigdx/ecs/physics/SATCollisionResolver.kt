package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Float4
import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.model.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

// FIXME: As the Bounding Box is expressed with line and not triangles,
//    the algorithm is not working any more.
// https://github.com/CasuallyCritical/OLC_PGEX_Collision3d/blob/eceaf621f1cfccd8bfe954e0b940a89857c6169f/olcPGEX_Collisions3D.h
class SATCollisionResolver : CollisionResolver {

    override fun collide(entityA: BoundingBox, entityB: BoundingBox): Boolean {
        val positionA = Mat4.identity()
        val positionB = Mat4.identity()

        // Stop collision resolving if the entity can't collide at the moment.
        if (!mightCollide(entityA, positionA, entityB, positionB)) {
            return false
        }

        val trianglesA = triangles(entityA, positionA)
        val trianglesB = triangles(entityB, positionB)

        // Get all axis
        val axis = trianglesA.flatMap { it.axis } + trianglesB.flatMap { it.axis }

        return trianglesA.any { a ->
            trianglesB.any { b ->
                axis.all { ax ->

                    val projA = project(ax, a)
                    val projB = project(ax, b)

                    projA.overlap(projB)
                }
            }
        }
    }

    private fun project(
        axis: Axis,
        triangle: Triangle
    ): Projection {
        var min = Float.POSITIVE_INFINITY
        var max = Float.NEGATIVE_INFINITY

        triangle.points.forEach { vertex ->
            val vertexPosition = Vector3(
                vertex.x,
                vertex.y,
                vertex.z
            )
            val proj = Vector3(axis.x, axis.y, axis.z).dot(vertexPosition)

            min = min(min, proj)
            max = max(max, proj)
        }

        return Projection(min, max)
    }

    private fun triangles(boundingBox: BoundingBox, transformation: Mat4): List<Triangle> {
        return boundingBox.order.map { boundingBox.vertices[it] }
            .chunked(3)
            .map {
                Triangle(
                    a = (transformation * it[0].position.asFloat4()).xyz,
                    b = (transformation * it[1].position.asFloat4()).xyz,
                    c = (transformation * it[2].position.asFloat4()).xyz
                )
            }
    }

    data class Triangle(val a: Float3, val b: Float3, val c: Float3) {

        val points by lazy { listOf(a, b, c) }

        val axis by lazy {
            points.mapIndexed { index, point1 ->
                val next = (index + 1) % points.size
                val point2 = points[next]
                Vector3(
                    point1.x - point2.x,
                    point1.y - point2.y,
                    point1.z - point2.z
                )
                    .normal()
                    .normalize()
            }.map {
                Axis(it.x, it.y, it.z)
            }
        }
    }

    data class Axis(val x: Float, val y: Float, val z: Float)

    data class Projection(val min: Float, val max: Float) {

        fun overlap(p2: Projection): Boolean {
            // Check if this projection overlaps with the passed one
            return !(p2.max < min || max < p2.min)
        }
    }

    fun Position.asFloat4(): Float4 = Float4(this.x, this.y, this.z, 1f)

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
