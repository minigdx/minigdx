package threed.file

import threed.entity.Color
import threed.entity.Mesh
import threed.entity.Vertice
import threed.math.Vector3

object MeshReader {

    private data class MeshBuilder(
        val name: String,
        val position: Vector3 = Vector3(),
        val rotation: Vector3 = Vector3(),
        val vertex: List<Vector3> = emptyList(),
        val color: List<Color> = emptyList(),
        val colorsOrder: List<Short> = emptyList(),
        val verticesOrder: List<Short> = emptyList()
    ) {

        fun build(): Mesh {
            val vertices = vertex.zip(color).map {
                Vertice(
                    position = it.first,
                    color = it.second
                )
            }

            return Mesh(
                name = name,
                position = position,
                rotation = rotation,
                vertices = vertices.toTypedArray(),
                verticesOrder = verticesOrder.toShortArray()
            )
        }
    }

    fun fromString(m3d: String): List<Mesh> {
        val lines = m3d.lines()
        val header = lines.firstOrNull()
        if (header?.startsWith("MINIGDX") != true) {
            throw IllegalArgumentException("The loaded file is not a valid MiniGDX 3D model file.")
        }

        val result = mutableListOf<Mesh>()
        lateinit var mesh: MeshBuilder
        lines.drop(1).forEach {
            when {
                it.startsWith("MESH") -> {
                    val (_, name) = it.split(" ")
                    mesh = MeshBuilder(name)
                }
                it.startsWith("POSITIONS") -> {
                    val positions = it.parseLine("POSITIONS")
                        .map { vec ->
                            val (x, y, z) = vec.trim().split(" ")
                            Vector3(x.toFloat(), y.toFloat(), z.toFloat())
                        }
                    mesh = mesh.copy(vertex = positions)
                }
                it.startsWith("COLOR_INDICES") -> {
                    val indices = it.parseLine("COLOR_INDICES")
                        .map { str -> str.toShort() }
                    mesh = mesh.copy(colorsOrder = indices)
                }
                it.startsWith("COLORS") -> {
                    val colors = it.parseLine("COLORS")
                        .map { vec ->
                            val (x, y, z, w) = vec.trim().split(" ")
                            Color(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
                        }
                    mesh = mesh.copy(color = colors)
                }

                it.startsWith("VERTEX_INDICES") -> {
                    val indices = it.parseLine("VERTEX_INDICES")
                        .map { str -> str.toShort() }
                    mesh = mesh.copy(verticesOrder = indices)
                }
                it.startsWith("ENDMESH") -> {
                    result.add(mesh.build())
                }
            }
        }

        return result
    }

    private fun String.parseLine(prefix: String): List<String> {
        return this.removePrefix(prefix)
            .trim()
            .split(", ")
            .filter { str -> str.isNotBlank() }
    }
}
