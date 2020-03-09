package com.github.dwursteisen.minigdx.file

import collada.EmptyAnimations
import collada.EmptyArmature
import collada.Model
import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.rotation
import com.github.dwursteisen.minigdx.entity.Color
import com.github.dwursteisen.minigdx.entity.Mesh
import com.github.dwursteisen.minigdx.entity.Vertice
import com.github.dwursteisen.minigdx.entity.animations.Animation
import com.github.dwursteisen.minigdx.entity.animations.Armature
import com.github.dwursteisen.minigdx.entity.animations.Frame
import com.github.dwursteisen.minigdx.entity.animations.Joint
import com.github.dwursteisen.minigdx.math.Vector3

object MeshReader {

    fun collada.Armature.toArmature(): Armature {
        val dic = mutableMapOf<String, Joint>()
        fun collada.Bone.toJoin(parent: Joint? = null): Joint {
            // FIXME: quickfix. This conversion should be in the parser
            val parentTransformation = parent?.globalBindTransaction ?: rotation(Float3(1f, 0f, 0f), -90f)
            val localTransformation = Mat4.of(*this.transformation.matrix)
            val b = Joint(
                id = this.id,
                parent = parent,
                children = emptyArray(),
                localBindTransformation = localTransformation,
                globalBindTransaction = parentTransformation * localTransformation,
                globalInverseBindTransformation = inverse(parentTransformation * localTransformation)
            )
            dic[this.id] = b
            b.children = this.childs.map { it.toJoin(b) }.toTypedArray()
            return b
        }

        val root = this.rootBone.toJoin()
        return Armature(
            rootJoint = root,
            allJoints = dic
        )
    }

    fun collada.Animations.toAnimations(): Animation {
        val frames = mutableMapOf<Float, Frame>()
        this.animations.forEach { join ->
            join.keyFrames.forEach { keys ->
                val mat4 = Mat4.of(*keys.transformation.matrix)

                val frame = frames[keys.time] ?: Frame()
                frame.rotation.keyframe = keys.time
                frame.rotation.joints[join.boneId] = Quaternion.from(mat4)

                frame.transformation.keyframe = keys.time
                frame.transformation.joints[join.boneId] = mat4.position.let {
                    Vector3(it.x, it.y, it.z)
                }

                frames[keys.time] = frame
            }
        }

        return Animation(
            duration = frames.keys.max() ?: 0f,
            rotations = frames.map { it.value.rotation }.toTypedArray(),
            transformations = frames.map { it.value.transformation }.toTypedArray()
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
            is collada.Animations -> anim.toAnimations()
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
        return Triple(mesh, armature, animations)
    }
}
