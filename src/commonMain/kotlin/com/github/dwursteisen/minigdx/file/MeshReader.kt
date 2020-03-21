package com.github.dwursteisen.minigdx.file

import collada.EmptyAnimations
import collada.EmptyArmature
import collada.InfluenceData
import collada.Model
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.github.dwursteisen.minigdx.entity.Color
import com.github.dwursteisen.minigdx.entity.Influence
import com.github.dwursteisen.minigdx.entity.JointsIndex
import com.github.dwursteisen.minigdx.entity.Mesh
import com.github.dwursteisen.minigdx.entity.Vertice
import com.github.dwursteisen.minigdx.entity.animations.Animation
import com.github.dwursteisen.minigdx.entity.animations.Armature
import com.github.dwursteisen.minigdx.entity.animations.Joint
import com.github.dwursteisen.minigdx.entity.animations.JointId
import com.github.dwursteisen.minigdx.entity.animations.KeyFrame
import com.github.dwursteisen.minigdx.math.Vector3

object MeshReader {

    fun collada.Armature.toArmature(): Pair<Armature, List<String>> {
        val joints = mutableListOf<Joint>()
        val mappingIds = mutableListOf<String>()

        fun collada.Bone.toJoin(parent: Joint? = null): Joint {
            val parentTransformation = parent?.globalInverseBindTransformation ?: Mat4.identity()
            val localTransformation = Mat4.of(*this.transformation.matrix)
            val b = Joint(
                id = joints.size,
                parent = parent,
                children = emptyArray(),
                localBindTransformation = localTransformation,
                globalInverseBindTransformation = parentTransformation * localTransformation
            )
            joints.add(b)
            mappingIds.add(this.id)
            b.children = this.childs.map { it.toJoin(b) }.toTypedArray()
            return b
        }

        val root = this.rootBone.toJoin()
        return Armature(
            rootJoint = root,
            allJoints = joints
                .onEach { it.globalInverseBindTransformation = inverse(it.globalInverseBindTransformation) }
                .map { it.id to it }
                .toMap()
        ) to mappingIds
    }

    fun collada.Animations.toAnimations(boneIdToJointIds: List<String>, reference: Armature): Animation {
        val frames = mutableMapOf<Float, Armature>()
        // Update all local matrix
        this.animations.forEach { join ->
            join.keyFrames.forEach { keys ->
                val pose = frames.getOrPut(keys.time) { reference.copy() }

                val jointId = boneIdToJointIds.indexOf(join.boneId)

                val localTransformation = Mat4.of(*keys.transformation.matrix)
                pose[jointId].localBindTransformation = localTransformation
            }
        }

        // Update all global matrix
        frames.values.forEach {
            it.traverse { joint ->
                val parent = joint.parent?.globalInverseBindTransformation ?: Mat4.identity()
                joint.globalInverseBindTransformation = parent * joint.localBindTransformation
            }

            it.traverse { joint ->
                joint.globalInverseBindTransformation = inverse(joint.globalInverseBindTransformation)
            }
        }

        return Animation(
            duration = frames.keys.max() ?: 0f,
            keyFrames = frames.map { KeyFrame(it.key, it.value) }.toTypedArray()
        )
    }

    fun fromProtobuf(data: ByteArray): Triple<Mesh, Armature?, Animation?> {
        val model = Model.readProtobuf(data)
        return convertModel(model)
    }

    @ExperimentalStdlibApi
    fun fromJson(data: ByteArray): Triple<Mesh, Armature?, Animation?> {
        val model = Model.readJson(data)
        return convertModel(model)
    }

    private fun convertModel(model: Model): Triple<Mesh, Armature?, Animation?> {
        val m = model.mesh
        val a = model.armature
        val anim = model.animations

        val armature = when (a) {
            is collada.Armature -> a.toArmature()
            is EmptyArmature -> null
            else -> null
        }

        val boneIdToJointIds = armature?.second ?: emptyList()
        val animations = when (anim) {
            is collada.Animations -> {
                anim.toAnimations(boneIdToJointIds, armature!!.first)
            }
            is EmptyAnimations -> null
            else -> null
        }

        val mesh = Mesh(
            name = "todo",
            position = Vector3(),
            rotation = Vector3(),
            vertices = m.vertices.map { v ->
                Vertice(
                    position = Vector3(v.position.x, v.position.y, v.position.z),
                    normal = Vector3(v.normal.x, v.normal.y, v.normal.z),
                    color = Color(v.color.r, v.color.g, v.color.b, v.color.a),
                    influence = v.influence.toInfluence(boneIdToJointIds)
                )
            }.toTypedArray(),
            verticesOrder = m.verticesOrder.map { it.toShort() }.toShortArray()
        )
        return Triple(mesh, armature?.first, animations)
    }

    private fun collada.Influence.toInfluence(mapping: List<String>): Influence? {
        if (this.data.isEmpty()) {
            return null
        }
        if (this.data.size > 3) {
            println("WARNING! Your some vertex of your model are influend by more than 3 bones!")
        }

        fun convert(data: InfluenceData?): Pair<JointId, Float> {
            if (data == null) {
                return -1 to 1f
            }
            val jointId = mapping.indexOf(data.boneId)
            val weight = data.weight
            return jointId to weight
        }

        // We support only 3 bones max per vertex
        val (a, wa) = convert(this.data.getOrNull(0))
        val (b, wb) = convert(this.data.getOrNull(1))
        val (c, wc) = convert(this.data.getOrNull(2))

        return Influence(
            joinIds = JointsIndex(a, b, c),
            weight = Vector3(wa, wb, wc)
        )
    }
}
