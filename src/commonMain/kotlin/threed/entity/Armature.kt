package threed.entity

import com.curiouscreature.kotlin.math.Mat4

class Bone(
    /**
     * ID of the bone.
     */
    val id: String,
    /**
     * Parent of the bone. null if none.
     */
    val parent: Bone?,
    /**
     * Childs of this bone. Empty if none.
     */
    var childs: Array<Bone>,
    /**
     *  Transformation relative to the parent.
     */
    val localTransformation: Mat4,
    /**
     * Global transformation.
     */
    val globalTransaction: Mat4
)

class Armature(
    val rootBone: Bone,
    val allBones: Map<String, Bone>
)
