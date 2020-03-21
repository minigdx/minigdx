package com.github.dwursteisen.minigdx.entity.animations

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.MeshReader
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test

class ArmatureTest {

    lateinit var armature: Armature

    @Before
    fun setUp() {
        FileHandler().readData("src/commonMain/resources/cube_animation2.protobuf").onLoaded {
            val (_, loadedArmature, _) = MeshReader.fromProtobuf(it)
            armature = loadedArmature!!
        }
    }

    @Test
    fun readArmature() {
        assertEquals(2, armature.allJoints.count())
    }

    @Test
    fun copyArmature() {
        val copy = armature.copy()
        assertEquals(2, copy.allJoints.count())
        copy.allJoints.forEach { (id, joint) ->
            assertEquals(armature[id].localBindTransformation, joint.localBindTransformation)
            assertEquals(armature[id].globalInverseBindTransformation, joint.globalInverseBindTransformation)
        }
    }
}
