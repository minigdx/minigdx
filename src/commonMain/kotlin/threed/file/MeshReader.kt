package threed.file

import collada.EmptyArmature
import collada.Model
import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.rotation
import threed.entity.Armature
import threed.entity.Bone
import threed.entity.Color
import threed.entity.Mesh
import threed.entity.Vertice
import threed.math.Vector3

object MeshReader {

    fun collada.Armature.toArmature(): Armature {
        val dic = mutableMapOf<String, Bone>()
        fun collada.Bone.toBone(parent: Bone? = null): Bone {
            val parentTransformation = parent?.globalTransaction ?: rotation(Float3(1f, 0f, 0f), -90f)
            // FIXME: quickfix. This conversion should be in the parser
            val localTransformation = Mat4.of(*this.transformation.matrix)
            val b = Bone(
                id = this.id,
                parent = parent,
                childs = emptyArray(),
                localTransformation = localTransformation,
                globalTransaction = parentTransformation * localTransformation
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

    fun fromProtobuf(data: ByteArray): Pair<Mesh, Armature?> {
        val model = Model.readProtobuf(data)
        return convertModel(model)
    }

    @ExperimentalStdlibApi
    fun fromJson(data: ByteArray): Pair<Mesh, Armature?> {
        val model = Model.readJson(data)
        return convertModel(model)
    }

    private fun convertModel(model: Model): Pair<Mesh, Armature?> {
        val m = model.mesh
        val a = model.armature

        val armature = when (a) {
            is collada.Armature -> a.toArmature()
            is EmptyArmature -> null
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
}
