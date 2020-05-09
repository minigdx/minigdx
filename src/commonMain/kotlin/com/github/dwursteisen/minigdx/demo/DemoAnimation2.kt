package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoAnimation2 : Game {

    override val worldResolution = WorldResolution(200, 200)

    private val camera = Camera3D.perspective(45, worldResolution.ratio, 1, 100)

    private val program = DefaultShaders.create3d()

    private val animatedModel: AnimatedModel by fileHandler.get("monkey_animation_gltf.protobuf")

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -5)
        animatedModel.rotateY(90f)
        animationNames.addAll(animatedModel.animationsName)
        animatedModel.switchAnimation(animationNames[currentAnimation])
        animatedModel.drawJoint = true
    }

    private var currentAnimation = 0
    private val animationNames = mutableListOf<String>()

    override fun render(delta: Seconds) {

        if (inputs.isKeyPressed(Key.F)) {
            animatedModel.rotateY(10f * delta)
        } else if (inputs.isKeyPressed(Key.H)) {
            animatedModel.rotateY(-10f * delta)
        }

        if (inputs.isKeyJustPressed(Key.G)) {
            currentAnimation = (currentAnimation + 1) % animationNames.size
            animatedModel.switchAnimation(animationNames[currentAnimation])
        }

        animatedModel.update(delta * 0.5f)
        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            animatedModel.draw(it)
        }
    }
}
