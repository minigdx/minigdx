package com.github.dwursteisen.minigdx.ecs.components.gl

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Color
import com.dwursteisen.minigdx.scene.api.model.Mesh
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.math.ImmutableVector3
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.math.toVector3
import com.github.dwursteisen.minigdx.shaders.Buffer
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

@Deprecated("use BoundingBoxComponent instead")
class BoundingBox private constructor(
    override var id: Id = Id(),
    override var isDirty: Boolean = true,
    /**
     * Is this box has been touched? It's mainly used to display it using a different color for debugging
     */
    var touch: Boolean = false,
    var owner: Entity? = null,
    // --- OpenGL fields ---
    val vertices: List<Vertex>,
    val order: List<Int>,
    var verticesBuffer: Buffer? = null,
    var orderBuffer: Buffer? = null,
    var colorBuffer: Buffer? = null,
) : GLResourceComponent {

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
        if (componentType == com.github.dwursteisen.minigdx.ecs.components.Position::class) {
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

        private val normal = Normal(0f, 0f, 0f)
        private val white = Color(1f, 1f, 1f)

        @ExperimentalStdlibApi
        fun from(mesh: Mesh): BoundingBox {
            val vertices = mesh.primitives.flatMap { it.vertices }
                .map { vertex -> Vector3(vertex.position.x, vertex.position.y, vertex.position.z) }
            val (min, max) = computeMinMax(vertices)
            return BoundingBox(
                vertices = listOf(
                    // 0
                    Vertex(
                        position = Position(
                            min.x,
                            max.y,
                            min.z
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 1
                    Vertex(
                        position = Position(
                            max.x,
                            max.y,
                            min.z
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 2
                    Vertex(
                        position = Position(
                            max.x,
                            min.y,
                            min.z
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 3
                    Vertex(
                        position = Position(
                            min.x,
                            min.y,
                            min.z
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 4
                    Vertex(
                        position = Position(
                            max.x,
                            max.y,
                            max.z
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 5
                    Vertex(
                        position = Position(
                            max.x,
                            min.y,
                            max.z
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 6
                    Vertex(
                        position = Position(
                            min.x,
                            max.y,
                            max.z
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 7
                    Vertex(
                        position = Position(
                            min.x,
                            min.y,
                            max.z
                        ),
                        normal = normal,
                        color = white
                    )
                ),
                order = listOf(
                    // face A
                    0, 1,
                    1, 2,
                    2, 3,
                    3, 0,
                    // face B
                    1, 4,
                    2, 5,
                    // face C
                    4, 6,
                    4, 5,
                    5, 7,
                    6, 7,
                    // face D,
                    6, 0,
                    3, 7
                )
            ).apply {
                this._rawMin.set(min)
                this._rawMax.set(max)
            }
        }

        fun default(): BoundingBox {
            val scale = Mat4.identity()

            val a = (scale * translation(Float3(-1f, 1f, 1f))).translation.toVector3()
            val b = (scale * translation(Float3(1f, 1f, 1f))).translation.toVector3()
            val c = (scale * translation(Float3(1f, -1f, 1f))).translation.toVector3()
            val d = (scale * translation(Float3(-1f, -1f, 1f))).translation.toVector3()
            val e = (scale * translation(Float3(1f, 1f, -1f))).translation.toVector3()
            val f = (scale * translation(Float3(1f, -1f, -1f))).translation.toVector3()
            val g = (scale * translation(Float3(-1f, 1f, -1f))).translation.toVector3()
            val h = (scale * translation(Float3(-1f, -1f, -1f))).translation.toVector3()
            return BoundingBox(
                vertices = listOf(
                    Vertex(
                        position = Position(a.x, a.y, a.z),
                        normal = normal,
                        color = white
                    ),
                    Vertex(
                        position = Position(b.x, b.y, b.z),
                        normal = normal,
                        color = white
                    ),
                    Vertex(
                        position = Position(c.x, c.y, c.z),
                        normal = normal,
                        color = white
                    ),
                    Vertex(
                        position = Position(d.x, d.y, d.z),
                        normal = normal,
                        color = white
                    ),
                    Vertex(
                        position = Position(e.x, e.y, e.z),
                        normal = normal,
                        color = white
                    ),
                    Vertex(
                        position = Position(f.x, f.y, f.z),
                        normal = normal,
                        color = white
                    ),
                    Vertex(
                        position = Position(g.x, g.y, g.z),
                        normal = normal,
                        color = white
                    ),
                    Vertex(
                        position = Position(h.x, h.y, h.z),
                        normal = normal,
                        color = white
                    )
                ),
                order = listOf(
                    // face A
                    0, 1,
                    1, 2,
                    2, 3,
                    3, 0,
                    // face B
                    1, 4,
                    2, 5,
                    // face C
                    4, 6,
                    4, 5,
                    5, 7,
                    6, 7,
                    // face D,
                    6, 0,
                    3, 7
                )
            ).also {
                val pts = listOf(a, b, c, d, e, f, g, h)
                val (min, max) = computeMinMax(pts)
                it._rawMin.set(min)
                it._rawMax.set(max)
            }
        }
    }
}
