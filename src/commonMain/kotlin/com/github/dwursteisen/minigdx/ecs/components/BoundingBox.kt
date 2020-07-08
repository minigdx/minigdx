package com.github.dwursteisen.minigdx.ecs.components

import com.dwursteisen.minigdx.scene.api.model.Color
import com.dwursteisen.minigdx.scene.api.model.Mesh
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.buffer.Buffer
import kotlin.math.max
import kotlin.math.min

data class BoundingBox(
    val vertices: List<Vertex>,
    val order: List<Int>,
    var verticesBuffer: Buffer? = null,
    var orderBuffer: Buffer? = null,
    var colorBuffer: Buffer? = null
) : Component {

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
                    // front
                    0, 1, 2,
                    2, 3, 0,
                    // right
                    1, 6, 5,
                    6, 2, 1,
                    // back
                    7, 6, 5,
                    5, 4, 7,
                    // left
                    4, 3, 0,
                    4, 0, 7,
                    // bottom
                    4, 5, 1,
                    1, 0, 4,
                    // top
                    3, 2, 6,
                    6, 7, 3
                )
            )
        }
    }
}
