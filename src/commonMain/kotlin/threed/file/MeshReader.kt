package threed.file

import collada.EmptyArmature
import collada.Model
import com.curiouscreature.kotlin.math.Mat4
import kotlinx.serialization.protobuf.ProtoBuf
import threed.entity.*
import threed.math.Vector3

object MeshReader {

    private data class MeshBuilder(
        val name: String,
        val position: Vector3 = Vector3(),
        val rotation: Vector3 = Vector3(),
        val vertex: List<Vector3> = emptyList(),
        val color: List<Color> = emptyList(),
        val normals: List<Vector3> = emptyList(),
        val colorsOrder: List<Short> = emptyList(),
        val verticesOrder: List<Short> = emptyList(),
        val normalOrder: List<Short> = emptyList()
    ) {

        fun build(): Mesh {
            val createdVertice = mutableMapOf<Triple<Short, Short, Short>, Pair<Int, Vertice>>()
            var index = 0
            val vertices = verticesOrder.zip(colorsOrder).zip(normalOrder).map {
                Triple(it.first.first, it.first.second, it.second)
            }.map {
                createdVertice.getOrPut(it) {
                    index++ to Vertice(
                        position = vertex[it.first.toInt()],
                        color = color[it.second.toInt()],
                        normal = normals[it.third.toInt()]
                    )
                }
            }

            return Mesh(
                name = name,
                position = position,
                rotation = rotation,
                vertices = vertices.distinctBy { it.first }.map { it.second }.toTypedArray(),
                verticesOrder = vertices.map { it.first.toShort() }.toShortArray()
            )
        }
    }

    fun collada.Armature.toArmature(): Armature {
        val dic = mutableMapOf<String, Bone>()
        fun collada.Bone.toBone(parent: Bone? = null): Bone {
            val b = Bone(
                id = this.id,
                parent = parent,
                childs = emptyArray(),
                transformation = Mat4.of(*this.transformation.matrix)
            )
            dic[this.id] = b
            b.childs = this.childs.map { it.toBone(b) }.toTypedArray()
            return b
        }
        val root = this.rootBone.toBone()
        return Armature(
            rootBone = root,
            allBones = dic
        )
    }

    fun fromByteArray(data: ByteArray): Pair<Mesh, Armature?> {
        val model = Model.readProtobuf(data)
        val m = model.mesh
        val a = model.armature

        val armature = when(a) {
            is collada.Armature -> a.toArmature()
            is collada.EmptyArmature -> null
            else -> null
        }

        return Mesh(
                name = "todo",
                position = Vector3(),
                rotation = Vector3(),
                vertices = m.vertices.map { v ->
                    Vertice(
                        position = Vector3(v.position.x, v.position.y, v.position.z),
                        normal = Vector3(v.normal.x, v.normal.y, v.normal.z),
                        color = Color(v.color.r, v.color.g, v.color.b, v.color.a)
                    )
                }.toTypedArray(),
                verticesOrder = m.verticesOrder.map { it.toShort() }.toShortArray()
            ) to armature
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
                            Color(x.toFloat(), y.toFloat(), z.toFloat(), 1.0f)
                        }
                    mesh = mesh.copy(color = colors)
                }
                it.startsWith("NORMALS") -> {
                    val normals = it.parseLine("NORMALS")
                        .map { vec ->
                            val (x, y, z) = vec.trim().split(" ")
                            Vector3(x.toFloat(), y.toFloat(), z.toFloat())
                        }
                    mesh = mesh.copy(normals = normals)
                }
                it.startsWith("NORMAL_INDICES") -> {
                    val indices = it.parseLine("NORMAL_INDICES")
                        .map { str -> str.toShort() }
                    mesh = mesh.copy(normalOrder = indices)
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
