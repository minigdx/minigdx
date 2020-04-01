package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.entity.animations.Animation
import com.github.dwursteisen.minigdx.entity.animations.KeyFrame

class AnimatedModelLoader : FileLoader<AnimatedModel> {

    @ExperimentalStdlibApi
    override fun load(filename: String, content: String): AnimatedModel {
        return load(filename, content.encodeToByteArray())
    }

    @ExperimentalStdlibApi
    override fun load(filename: String, content: ByteArray): AnimatedModel {
        val (mesh, armature, animations) = if (filename.endsWith(".json")) {
            MeshReader.fromJson(content)
        } else {
            MeshReader.fromProtobuf(content)
        }

        return AnimatedModel(
            animation = animations ?: Animation(
                0f, keyFrames = arrayOf(
                    KeyFrame(0f, armature!!.copy())
                )
            ),
            mesh = mesh,
            armature = armature!!,
            drawJoint = true
        )
    }
}
