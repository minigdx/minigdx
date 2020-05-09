package com.github.dwursteisen.minigdx.file

import collada.Bone
import collada.CameraType
import collada.EmptyAnimations
import collada.EmptyArmature
import collada.InfluenceData
import collada.Model as ColladaModel
import collada.Transformation
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.degrees
import com.curiouscreature.kotlin.math.inverse
import com.github.dwursteisen.minigdx.entity.animations.Animation
import com.github.dwursteisen.minigdx.entity.animations.Armature
import com.github.dwursteisen.minigdx.entity.animations.Joint
import com.github.dwursteisen.minigdx.entity.animations.JointId
import com.github.dwursteisen.minigdx.entity.animations.KeyFrame
import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.primitives.Color
import com.github.dwursteisen.minigdx.entity.primitives.Influence
import com.github.dwursteisen.minigdx.entity.primitives.JointsIndex
import com.github.dwursteisen.minigdx.entity.primitives.Mesh
import com.github.dwursteisen.minigdx.entity.primitives.Vertice
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.random.Random

data class ModelDescription(
    val model: Model,
    val animations: Map<String, Animation> = emptyMap(),
    val cameras: Map<String, Camera3D> = emptyMap()
)

object ModelReader {

    fun collada.Armature.toArmature(): Pair<Armature, List<String>> {

        fun collada.Bone.toJoin(
            parent: Joint? = null,
            joints: MutableList<Joint>,
            mappingIds: MutableList<String>
        ): Joint {
            val parentTransformation = parent?.globalBindTransformation ?: Mat4.identity()
            val localTransformation = Mat4.of(*this.transformation.matrix)
            val globalBindTransformation = parentTransformation * localTransformation
            val b = Joint(
                id = joints.size,
                parent = parent,
                children = emptyArray(),
                localBindTransformation = localTransformation,
                globalBindTransformation = globalBindTransformation,
                globalInverseBindTransformation = inverse(globalBindTransformation),
                animationTransformation = Mat4.identity()
            )

            joints.add(b)
            mappingIds.add(this.id)

            b.children = this.childs.map { it.toJoin(b, joints, mappingIds) }.toTypedArray()
            return b
        }

        val joints = mutableListOf<Joint>()
        val mappingIds = mutableListOf<String>()

        val root = this.rootBone.toJoin(
            parent = null,
            joints = joints,
            mappingIds = mappingIds
        )
        return Armature(
            rootJoint = root,
            allJoints = joints
                .map { it.id to it }
                .toMap()
        ) to mappingIds
    }

    fun collada.Animations.toAnimations(boneIdToJointIds: List<String>, reference: Armature): Map<String, Animation> {
        val result = mutableMapOf<String, Animation>()

        this.animations.forEach { animation ->
            val frames = mutableMapOf<Float, Armature>()
            animation.keyFrames.forEach { keys ->
                val pose = frames.getOrPut(keys.time) { reference.copy() }
                keys.transformations.forEach { (boneId, transformation) ->
                    val jointId = boneIdToJointIds.indexOf(boneId)
                    // local animation transform
                    val animationMatrix = Mat4.of(*transformation.matrix)
                    pose[jointId].localBindTransformation = animationMatrix
                }
            }

            // update all matrix
            frames.forEach { frame ->
                frame.value.traverse { joint ->
                    val parentGlobalBindTransform = joint.parent?.globalBindTransformation ?: Mat4.identity()
                    val globalBindTransform = parentGlobalBindTransform * joint.localBindTransformation

                    joint.globalBindTransformation = globalBindTransform
                    joint.globalInverseBindTransformation = inverse(globalBindTransform)
                }
            }

            result[animation.name] = Animation(
                duration = frames.keys.max() ?: 0f,
                keyFrames = frames.map { KeyFrame(it.key, it.value) }.toTypedArray()
            )
        }
        return result
    }

    fun fromProtobuf(data: ByteArray): ModelDescription {
        val model = ColladaModel.readProtobuf(data)
        return convertModel(model)
    }

    @ExperimentalStdlibApi
    fun fromJson(data: ByteArray): ModelDescription {
        val model = ColladaModel.readJson(data)
        return convertModel(model)
    }

    private fun convertModel(model: ColladaModel): ModelDescription {
        val m = model.mesh
        val a = model.armature
        val anim = model.animations

        val armature = when (a) {
            is collada.Armature -> a.toArmature()
            is EmptyArmature -> collada.Armature(
                Bone(
                    id = "root",
                    childs = emptyList(),
                    transformation = Transformation(Mat4.identity().toFloatArray()),
                    inverseBindPose = Transformation(Mat4.identity().toFloatArray()),
                    weights = emptyList()
                )
            ).toArmature()
            else -> collada.Armature(
                Bone(
                    id = "root",
                    childs = emptyList(),
                    transformation = Transformation(Mat4.identity().toFloatArray()),
                    inverseBindPose = Transformation(Mat4.identity().toFloatArray()),
                    weights = emptyList()
                )
            ).toArmature()
        }

        val boneIdToJointIds = armature.second
        val animations = when (anim) {
            is collada.Animations -> anim.toAnimations(boneIdToJointIds, armature.first)
            is EmptyAnimations -> emptyMap()
            else -> emptyMap()
        }

        val mesh = Mesh(
            name = generateName(),
            position = Vector3(),
            rotation = Vector3(),
            vertices = m.vertices.map { v ->
                Vertice(
                    position = Vector3(v.position.x, v.position.y, v.position.z),
                    normal = Vector3(v.normal.x, v.normal.y, v.normal.z),
                    color = Color(
                        v.color.r,
                        v.color.g,
                        v.color.b,
                        v.color.a
                    ),
                    influence = v.influence.toInfluence(boneIdToJointIds)
                )
            }.toTypedArray(),
            verticesOrder = m.verticesOrder.map { it.toShort() }.toShortArray()
        )

        return ModelDescription(
            model = Model(mesh, armature.first),
            animations = animations,
            cameras = model.cameras.map { cam ->
                println("data --> " + cam)
                val camera = when (cam.type) {
                    CameraType.PERSPECTIVE -> Camera3D.perspective(
                        fov = degrees(cam.parameters.perspectiveFov),
                        aspect = 1, // FIXME: it should be updated at the creation of the game?
                        far = cam.parameters.zFar,
                        near = cam.parameters.zNear
                    ).also {
                        val mat = Mat4.of(*cam.transformation.matrix)
                        // FIXME: the - should be in the parser
                        it.setTranslate(mat.translation.x, mat.translation.y, -mat.translation.z)
                        it.setRotation(Quaternion.from(mat))
                        it.setScale(mat.scale.x, mat.scale.y, mat.scale.z)
                    }

                    CameraType.ORTHOGRAPHIC -> Camera3D.orthographic()
                }

                println(camera.modelMatrix)
                cam.name to camera
            }.toMap()
        )
    }

    private fun generateName(): String {
        return (0..10).map { Random.nextInt() }
            .joinToString("")
    }

    private fun collada.Influence.toInfluence(mapping: List<String>): Influence? {
        if (this.data.isEmpty()) {
            return Influence(
                joinIds = JointsIndex(0, 0, 0),
                weight = Vector3(1f, 0f, 0f)
            )
        }
        if (this.data.size > 3) {
            println("WARNING! Your some vertex of your model are influenced by more than 3 bones!")
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
