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
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

data class BoundingBox(
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
    // FIXME: should be based on min/max instead
    val radius: Float = radius(vertices)
        get() {
            updateIfNeeded()
            return field
        }

    private var needsToBeUpdated = true

    private fun updateIfNeeded() {
        if (!needsToBeUpdated) return
        val transformation = owner?.position?.combinedTransformation ?: Mat4.identity()
        _min.set((transformation * translation(_rawMin.toFloat3())).translation.toVector3())
        _max.set((transformation * translation(_rawMax.toFloat3())).translation.toVector3())

        this._size.set(
            _max.x - _min.x,
            _max.y - _min.y,
            _max.z - _min.z
        )
        this._center.set(
            _size.x * 0.5f + _min.x,
            _size.y * 0.5f + _min.y,
            _size.z * 0.5f + +_min.z,
        )
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

    private class BoxBuilder(
        var minX: Float? = null,
        var maxX: Float? = null,
        var minY: Float? = null,
        var maxY: Float? = null,
        var minZ: Float? = null,
        var maxZ: Float? = null
    )

    companion object {

        private val normal = Normal(0f, 0f, 0f)
        private val white = Color(1f, 1f, 1f)

        private fun radius(vertices: List<Vertex>): Float {
            return vertices.map { it.position }
                .flatMap { listOf(abs(it.x), abs(it.y), abs(it.z)) }
                .maxOrNull() ?: 0f
        }

        @ExperimentalStdlibApi
        fun from(mesh: Mesh): BoundingBox {
            val vertices = mesh.primitives.flatMap { it.vertices }
            val builder = vertices.fold(BoxBuilder()) { builder, vertex ->
                builder.minX = min(builder.minX ?: vertex.position.x, vertex.position.x)
                builder.maxX = max(builder.maxX ?: vertex.position.x, vertex.position.x)
                builder.minY = min(builder.minY ?: vertex.position.y, vertex.position.y)
                builder.maxY = max(builder.maxY ?: vertex.position.y, vertex.position.y)
                builder.minZ = min(builder.minZ ?: vertex.position.z, vertex.position.z)
                builder.maxZ = max(builder.maxZ ?: vertex.position.z, vertex.position.z)
                builder
            }

            return BoundingBox(
                vertices = listOf(
                    // 0
                    Vertex(
                        position = Position(
                            builder.minX!!,
                            builder.maxY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 1
                    Vertex(
                        position = Position(
                            builder.maxX!!,
                            builder.maxY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 2
                    Vertex(
                        position = Position(
                            builder.maxX!!,
                            builder.minY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 3
                    Vertex(
                        position = Position(
                            builder.minX!!,
                            builder.minY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 4
                    Vertex(
                        position = Position(
                            builder.maxX!!,
                            builder.maxY!!,
                            builder.maxZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 5
                    Vertex(
                        position = Position(
                            builder.maxX!!,
                            builder.minY!!,
                            builder.maxZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 6
                    Vertex(
                        position = Position(
                            builder.minX!!,
                            builder.maxY!!,
                            builder.maxZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 7
                    Vertex(
                        position = Position(
                            builder.minX!!,
                            builder.minY!!,
                            builder.maxZ!!
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
                this._rawMin.set(
                    builder.minX!!.toFloat(),
                    builder.minY!!.toFloat(),
                    builder.minZ!!.toFloat()
                )

                this._rawMax.set(
                    builder.maxX!!.toFloat(),
                    builder.maxY!!.toFloat(),
                    builder.maxZ!!.toFloat()
                )
            }
        }

        fun default(): BoundingBox {
            val scale = Mat4.identity()

            val a = (scale * translation(Float3(-1f, 1f, 1f))).translation
            val b = (scale * translation(Float3(1f, 1f, 1f))).translation
            val c = (scale * translation(Float3(1f, -1f, 1f))).translation
            val d = (scale * translation(Float3(-1f, -1f, 1f))).translation
            val e = (scale * translation(Float3(1f, 1f, -1f))).translation
            val f = (scale * translation(Float3(1f, -1f, -1f))).translation
            val g = (scale * translation(Float3(-1f, 1f, -1f))).translation
            val h = (scale * translation(Float3(-1f, -1f, -1f))).translation
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
            ).apply {
                val pts = listOf(a, b, c, d, e, f, g, h)
                val builder = pts.fold(BoxBuilder()) { builder, vector ->
                    builder.minX = min(builder.minX ?: vector.x, vector.x)
                    builder.maxX = max(builder.maxX ?: vector.x, vector.x)
                    builder.minY = min(builder.minY ?: vector.y, vector.y)
                    builder.maxY = max(builder.maxY ?: vector.y, vector.y)
                    builder.minZ = min(builder.minZ ?: vector.z, vector.z)
                    builder.maxZ = max(builder.maxZ ?: vector.z, vector.z)
                    builder
                }
                this._rawMin.set(
                    builder.minX!!.toFloat(),
                    builder.minY!!.toFloat(),
                    builder.minZ!!.toFloat()
                )

                this._rawMax.set(
                    builder.maxX!!.toFloat(),
                    builder.maxY!!.toFloat(),
                    builder.maxZ!!.toFloat()
                )
            }
        }
    }
}
