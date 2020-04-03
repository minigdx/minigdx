package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.entity.Camera
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoPlanet : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera.create(45, worldSize.ratio, 1, 1000)

    private val program = DefaultShaders.create()

    private val model: Drawable by fileHandler.get("planet.protobuf")

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -200)
    }

    private var rotationStart: Float? = null
    private var currentRotation: Float = 0f
    private var xStart = 0f

    override fun render(delta: Seconds) {
        // --- act ---
        val cursor = inputs.isTouched(TouchSignal.TOUCH1)
        if (cursor != null) {
            if (rotationStart == null) {
                rotationStart = model.mesh.rotation.y
                xStart = cursor.x
            }

            val screenWidth = gl.screen.width
            val factor = (cursor.x - screenWidth * 0.5f) / screenWidth
            currentRotation = factor * 180f
            rotationStart?.run {
                model.mesh.setRotationY(this + currentRotation)
            }
        } else {
            rotationStart = null
            model.mesh.rotateY(delta * 10)
        }

        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            model.draw(it)
        }
    }
}
