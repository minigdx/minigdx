package com.github.dwursteisen.minigdx.entity.animations

import com.curiouscreature.kotlin.math.Mat4

class Joint(
    /**
     * ID of the bone.
     */
    val id: String,
    /**
     * Parent of the bone. null if none.
     */
    val parent: Joint?,
    /**
     * Childs of this bone. Empty if none.
     */
    var children: Array<Joint>,

    var animatedTransformation: Mat4 = Mat4.identity(),
    /**
     *  Transformation relative to the parent.
     */
    val localBindTransformation: Mat4,
    /**
     * Global transformation.
     */
    @Deprecated("utiliser la version inverse")
    val globalBindTransaction: Mat4,

    val globalInverseBindTransformation: Mat4
)

class Armature(
    val rootJoint: Joint,
    val allJoints: Map<String, Joint>
) {
    fun traverse(joint: Joint = rootJoint, block: (Joint) -> Unit) {
        block(rootJoint)
        rootJoint.children.forEach(block)
    }
}
