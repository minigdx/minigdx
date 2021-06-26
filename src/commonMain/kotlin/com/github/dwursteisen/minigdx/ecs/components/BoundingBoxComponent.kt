package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.graph.Model
import com.github.dwursteisen.minigdx.math.ImmutableVector3
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.math.toVector3
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

class BoundingBoxComponent private constructor(
    /**
     * Is this box has been touched? It's mainly used to display it using a different color for debugging
     */
    var touch: Boolean = false,
    var owner: Entity? = null
) : Component {

    private val _rawMin = Vector3()
    private val _rawMax = Vector3()

    private val _min = Vector3()
    private val _max = Vector3()

    private val _center = Vector3()
    private val _size = Vector3()

    val localMin: ImmutableVector3 = ImmutableVector3(_rawMin)
    val localMax: ImmutableVector3 = ImmutableVector3(_rawMax)

    /**
     * Minus points of the bounding box (ie: lower left)
     */
    val min: ImmutableVector3 = ImmutableVector3(_min)
        get() {
            updateIfNeeded()
            return field
        }

    /**
     * Maximun points of  the bounding box
     */
    val max: ImmutableVector3 = ImmutableVector3(_max)
        get() {
            updateIfNeeded()
            return field
        }

    /**
     * Center of the bounding box.
     */
    val center: ImmutableVector3 = ImmutableVector3(_center)
        get() {
            updateIfNeeded()
            return field
        }

    /**
     * Size of the bounding box
     */
    val size: ImmutableVector3 = ImmutableVector3(_size)
        get() {
            updateIfNeeded()
            return field
        }

    /**
     *
     */
    val radius: Float
        get() {
            updateIfNeeded()
            return _max.copy().sub(_center).length()
        }

    val combinedTransformation: Mat4
        get() {
            return owner?.position?.transformation ?: Mat4.identity()
        }

    var edges: List<Vector3> = emptyList()
        get() {
            updateIfNeeded()
            return field
        }
        private set

    private var needsToBeUpdated = true

    private fun updateIfNeeded() {
        if (!needsToBeUpdated) return
        val transformation = combinedTransformation

        val edges = listOf(
            (transformation * translation(Float3(_rawMin.x, _rawMin.y, _rawMin.z))).translation.toVector3(),
            (transformation * translation(Float3(_rawMin.x, _rawMin.y, _rawMax.z))).translation.toVector3(),
            (transformation * translation(Float3(_rawMin.x, _rawMax.y, _rawMin.z))).translation.toVector3(),
            (transformation * translation(Float3(_rawMin.x, _rawMax.y, _rawMax.z))).translation.toVector3(),
            (transformation * translation(Float3(_rawMax.x, _rawMin.y, _rawMin.z))).translation.toVector3(),
            (transformation * translation(Float3(_rawMax.x, _rawMin.y, _rawMax.z))).translation.toVector3(),
            (transformation * translation(Float3(_rawMax.x, _rawMax.y, _rawMin.z))).translation.toVector3(),
            (transformation * translation(Float3(_rawMax.x, _rawMax.y, _rawMax.z))).translation.toVector3(),
        )

        val (min, max) = computeMinMax(edges)
        this._min.set(min)
        this._max.set(max)

        this._size.set(
            _max.x - _min.x,
            _max.y - _min.y,
            _max.z - _min.z
        )
        this._center.set(
            _size.x * 0.5f + _min.x,
            _size.y * 0.5f + _min.y,
            _size.z * 0.5f + _min.z,
        )
        this.edges = edges
        needsToBeUpdated = false
    }

    fun contains(point: Vector3): Boolean {
        return min.x <= point.x && max.x >= point.x &&
            min.y <= point.y && max.y >= point.y &&
            min.z <= point.z && max.z >= point.z
    }

    override fun onAdded(entity: Entity) {
        owner = entity
    }

    override fun onRemoved(entity: Entity) {
        owner = null
    }

    override fun onComponentUpdated(componentType: KClass<out Component>) {
        if (componentType == Position::class) {
            needsToBeUpdated = true
        }
    }

    override fun onDetach(parent: Entity) {
        needsToBeUpdated = true
    }

    override fun onAttach(parent: Entity) {
        needsToBeUpdated = true
    }

    private class BoxBuilder(
        var minX: Float? = null,
        var maxX: Float? = null,
        var minY: Float? = null,
        var maxY: Float? = null,
        var minZ: Float? = null,
        var maxZ: Float? = null
    )

    companion object {

        fun default(): BoundingBoxComponent {
            val boundingBoxComponent = BoundingBoxComponent()
            boundingBoxComponent._rawMax.set(1, 1, 1)
            boundingBoxComponent._rawMin.set(-1, -1, -1)
            return boundingBoxComponent
        }

        fun from(model: Model): BoundingBoxComponent {
            val vectors = model.primitives.flatMap { primitive ->
                primitive.vertices
                    .toList()
                    .chunked(3)
                    .map { (x, y, z) -> Vector3(x, y, z) }
            }

            val (min, max) = computeMinMax(vectors)

            val component = BoundingBoxComponent()
            component._rawMin.set(min)
            component._rawMax.set(max)
            return component
        }

        private fun minNull(a: Float?, b: Float): Float {
            return min(a ?: b, b)
        }

        private fun maxNull(a: Float?, b: Float): Float {
            return max(a ?: b, b)
        }

        private fun computeMinMax(points: List<Vector3>): Pair<Vector3, Vector3> {
            val builder = points.fold(BoxBuilder()) { builder, vertex ->
                builder.minX = minNull(builder.minX, vertex.x)
                builder.minY = minNull(builder.minY, vertex.y)
                builder.minZ = minNull(builder.minZ, vertex.z)

                builder.maxX = maxNull(builder.maxX, vertex.x)
                builder.maxY = maxNull(builder.maxY, vertex.y)
                builder.maxZ = maxNull(builder.maxZ, vertex.z)
                builder
            }
            return Vector3(
                builder.minX!!.toFloat(),
                builder.minY!!.toFloat(),
                builder.minZ!!.toFloat()
            ) to Vector3(
                builder.maxX!!.toFloat(),
                builder.maxY!!.toFloat(),
                builder.maxZ!!.toFloat()
            )
        }
    }
}
