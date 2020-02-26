package threed.entity

import com.curiouscreature.kotlin.math.Mat4

class Bone(val id: String, val parent: Bone?, var childs: Array<Bone>, val transformation: Mat4)

class Armature(
    val rootBone: Bone,
    val allBones: Map<String, Bone>
)