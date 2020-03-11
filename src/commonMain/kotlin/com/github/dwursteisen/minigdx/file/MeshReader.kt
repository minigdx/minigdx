package com.github.dwursteisen.minigdx.file

import collada.EmptyAnimations
import collada.EmptyArmature
import collada.Model
import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.rotation
import com.github.dwursteisen.minigdx.entity.Color
import com.github.dwursteisen.minigdx.entity.Mesh
import com.github.dwursteisen.minigdx.entity.Vertice
import com.github.dwursteisen.minigdx.entity.animations.Animation
import com.github.dwursteisen.minigdx.entity.animations.Armature
import com.github.dwursteisen.minigdx.entity.animations.Joint
import com.github.dwursteisen.minigdx.entity.animations.KeyFrame
import com.github.dwursteisen.minigdx.math.Vector3

object MeshReader {

    fun collada.Armature.toArmature(): Pair<Armature, List<String>> {
        val joints = mutableListOf<Joint>()
        val mappingIds = mutableListOf<String>()
        fun collada.Bone.toJoin(parent: Joint? = null): Joint {
            // FIXME: quickfix. This conversion should be in the parser
            val parentTransformation = parent?.globalBindTransaction ?: rotation(Float3(1f, 0f, 0f), -90f)
            val localTransformation = Mat4.of(*this.transformation.matrix)
            val b = Joint(
                id = joints.size,
                parent = parent,
                children = emptyArray(),
                localBindTransformation = localTransformation,
                globalBindTransaction = parentTransformation * localTransformation,
                globalInverseBindTransformation = inverse(parentTransformation * localTransformation)
            )
            joints.add(b)
            mappingIds.add(this.id)
            b.children = this.childs.map { it.toJoin(b) }.toTypedArray()
            return b
        }

        val root = this.rootBone.toJoin()
        return Armature(
            rootJoint = root,
            allJoints = joints.map { it.id to it }.toMap()
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
        // update all globalMatrix
        frames.values.forEach {
            it.traverse { joint ->
                val local = joint.localBindTransformation
                val parent = joint.parent?.globalBindTransaction ?: Mat4.identity()
                joint.globalInverseBindTransformation = inverse(local * parent)
                joint.globalBindTransaction = local * parent
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

        val animations = when (anim) {
            is collada.Animations -> anim.toAnimations(armature?.second ?: emptyList(), armature!!.first)
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
                    color = Color(v.color.r, v.color.g, v.color.b, v.color.a)
                )
            }.toTypedArray(),
            verticesOrder = m.verticesOrder.map { it.toShort() }.toShortArray()
        )
        return Triple(mesh, armature?.first, animations)
    }
}
