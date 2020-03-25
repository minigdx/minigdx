package com.github.dwursteisen.minigdx.entity.animations

import com.curiouscreature.kotlin.math.Mat4

typealias JointId = Int

class Joint(
    /**
     * ID of the bone.
     */
    val id: JointId,
    /**
     * Parent of the bone. null if none.
     */
    val parent: Joint?,
    /**
     * Childs of this bone. Empty if none.
     */
    var children: Array<Joint>,

    /**
     *  Transformation relative to the parent.
     */
    var localBindTransformation: Mat4,
    /**
     * Global transformation.
     */
    var globalBindTransformation: Mat4,
    /**
     * Global Inverse transformation.
     */
    var globalInverseBindTransformation: Mat4,
    /**
     * Transformation of the current joint to
     */
    var animationTransformation: Mat4
)

class Armature(
    val rootJoint: Joint,
    val allJoints: Map<JointId, Joint>
) {
    operator fun get(jointId: JointId): Joint = allJoints.getValue(jointId)

    fun traverse(joint: Joint = rootJoint, block: (Joint) -> Unit) {
        block(rootJoint)
        rootJoint.children.forEach(block)
    }

    fun copy(): Armature {
        val allJoints = mutableMapOf<JointId, Joint>()
        fun _traverse(joint: Joint) {
            val copy = Joint(
                id = joint.id,
                parent = allJoints[joint.parent?.id],
                localBindTransformation = joint.localBindTransformation,
                globalBindTransformation = joint.globalBindTransformation,
                globalInverseBindTransformation = joint.globalInverseBindTransformation,
                animationTransformation = Mat4.identity(),
                children = arrayOf()
            )
            allJoints[joint.id] = copy
            joint.children.forEach {
                _traverse(it)
            }
            copy.children = joint.children.map { allJoints[it.id]!! }.toTypedArray()
        }
        _traverse(rootJoint)

        return Armature(
            rootJoint = allJoints[rootJoint.id]!!,
            allJoints = allJoints
        )
    }
}
