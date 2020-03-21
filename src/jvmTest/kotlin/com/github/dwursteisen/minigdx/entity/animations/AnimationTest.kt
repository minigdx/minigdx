package com.github.dwursteisen.minigdx.entity.animations

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.MeshReader
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test

class AnimationTest {

    lateinit var animation: Animation

    @Before
    fun setUp() {
        FileHandler().readData("src/commonMain/resources/cube_animation2.protobuf").onLoaded {
            val (_, _, loadedAnimation) = MeshReader.fromProtobuf(it)
            animation = loadedAnimation!!
        }
    }

    @Test
    fun getOnionFrames() {
        val (first, after) = animation.getOnionFrames(0f)
        assertNotNull(first)
        assertNotNull(after)
    }

    @Test
    fun keyframes() {
        fun validateArmature(armature: Armature) {
            val copy = armature.copy()
            // set global matrice
            copy.traverse {
                it.globalInverseBindTransformation = (it.parent?.globalInverseBindTransformation ?: Mat4.identity()) * it.localBindTransformation
            }

            // inverse matrice
            copy.traverse {
                it.globalInverseBindTransformation = inverse(it.globalInverseBindTransformation)
            }

            // check all joints
            armature.allJoints.forEach { id, joint ->
                assertEquals(copy[id].localBindTransformation, joint.localBindTransformation)
                assertEquals(copy[id].globalInverseBindTransformation, joint.globalInverseBindTransformation)
            }
        }

        assertEquals(30, animation.keyFrames.count())
        animation.keyFrames.forEach {
            validateArmature(it.pose)
        }
    }
}
