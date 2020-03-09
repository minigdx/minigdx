package com.github.dwursteisen.minigdx.entity.animations

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.MeshReader
import kotlin.test.assertNotNull
import org.junit.Test

class AnimationTest {

    @Test
    fun getOnionFrames() {
        FileHandler().readData("src/commonMain/resources/armature.protobuf").onLoaded {
            val (mesh, armature, animation) = MeshReader.fromProtobuf(it)

            val (first, after) = animation!!.getOnionFrames(0f)
            assertNotNull(first)
            assertNotNull(after)
        }
    }
}
