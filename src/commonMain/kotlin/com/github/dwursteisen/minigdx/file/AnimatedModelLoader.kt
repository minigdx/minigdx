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
        val description = if (filename.endsWith(".json")) {
            ModelReader.fromJson(content)
        } else {
            ModelReader.fromProtobuf(content)
        }

        val currentAnimation = if (description.animations.size != 1) {
            Animation(0f, keyFrames = arrayOf(KeyFrame(0f, description.model.pose!!.copy())))
        } else {
            description.animations.values.first()
        }
        return AnimatedModel(
            animation = currentAnimation,
            animations = description.animations,
            model = description.model,
            drawJoint = true
        )
    }
}
