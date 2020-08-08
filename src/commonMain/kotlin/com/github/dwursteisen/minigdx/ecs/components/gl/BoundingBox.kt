package com.github.dwursteisen.minigdx.ecs.components.gl

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.model.Boxe
import com.dwursteisen.minigdx.scene.api.model.Color
import com.dwursteisen.minigdx.scene.api.model.Mesh
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.shaders.Buffer
import kotlin.math.max
import kotlin.math.min

data class BoundingBox(
    val vertices: List<Vertex>,
    val order: List<Int>,
    val radius: Float = radius(vertices),
    var verticesBuffer: Buffer? = null,
    var orderBuffer: Buffer? = null,
    var colorBuffer: Buffer? = null,
    var touch: Boolean = false,
    override var isDirty: Boolean = true
) : GLResourceComponent {

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
                .flatMap { listOf(it.x, it.y, it.z) }
                .max() ?: 0f
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
                            builder.minY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 1
                    Vertex(
                        position = Position(
                            builder.maxX!!,
                            builder.minY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 2
                    Vertex(
                        position = Position(
                            builder.maxX!!,
                            builder.maxY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 3
                    Vertex(
                        position = Position(
                            builder.minX!!,
                            builder.maxY!!,
                            builder.minZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 4
                    Vertex(
                        position = Position(
                            builder.minX!!,
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
                            builder.maxY!!,
                            builder.maxZ!!
                        ),
                        normal = normal,
                        color = white
                    ),
                    // 6
                    Vertex(
                        position = Position(
                            builder.maxX!!,
                            builder.minY!!,
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
                    0, 1, 3,
                    1, 2, 3,
                    // face B
                    1, 5, 2,
                    2, 5, 6,
                    // face C
                    5, 4, 6,
                    6, 4, 7,
                    // face D,
                    4, 0, 7,
                    7, 0, 3,
                    // face E,
                    3, 2, 7,
                    7, 2, 6,
                    // face F
                    0, 1, 4,
                    4, 1, 5
                )
            )
        }

        fun from(boxe: Boxe): BoundingBox {
            val transformation = Mat4.fromColumnMajor(*boxe.transformation.matrix)
            val a = (translation(Float3(1f, 1f, 1f)) * transformation).translation
            val b = (translation(Float3(-1f, 1f, 1f)) * transformation).translation
            val c = (translation(Float3(1f, -1f, 1f)) * transformation).translation
            val d = (translation(Float3(-1f, -1f, 1f)) * transformation).translation
            val e = (translation(Float3(1f, 1f, -1f)) * transformation).translation
            val f = (translation(Float3(-1f, 1f, -1f)) * transformation).translation
            val g = (translation(Float3(1f, -1f, -1f)) * transformation).translation
            val h = (translation(Float3(-1f, -1f, -1f)) * transformation).translation
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
                    0, 1, 3,
                    1, 2, 3,
                    // face B
                    1, 5, 2,
                    2, 5, 6,
                    // face C
                    5, 4, 6,
                    6, 4, 7,
                    // face D,
                    4, 0, 7,
                    7, 0, 3,
                    // face E,
                    3, 2, 7,
                    7, 2, 6,
                    // face F
                    0, 1, 4,
                    4, 1, 5
                )
            )
        }
    }
}
