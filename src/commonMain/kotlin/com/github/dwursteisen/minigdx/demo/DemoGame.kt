package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldSize
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Cube
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.math.Vector2
import com.github.dwursteisen.minigdx.shaders.DefaultShaders
import kotlin.math.max
import kotlin.math.min

class Player(private val model: Cube = Cube("player")) : CanMove by model, CanDraw by model {

    private var jumpingVector = Vector2(0f, 0f)

    private var jumpingTime = 0f

    private var jumping = false

    fun update(delta: Float) {
        if (jumpingTime >= maxJumpTime || !jumping) {
            jumpingVector.y = max(jumpingVector.y - jumpSpeed * 2.2f * delta, -jumpSpeed * 2.2f)
            jumping = false
        }

        // TODO: deteck if ground touched
        // TODO: move the player on the ground
        if (position.y <= flowY && !jumping) {
            jumpingTime = 0f
            jumpingVector.y = 0f
        }
        translate(y = jumpingVector.y)
        jumping = false
    }

    fun jump(delta: Float) {
        jumpingVector.y = min(jumpingVector.y + jumpSpeed * delta, jumpSpeed)
        jumpingTime += delta
        jumping = true
    }

    companion object {
        private const val maxJumpTime = 0.2f
        private const val flowY = -10f
        private const val jumpSpeed = 5f
    }
}

class DemoGame : Game {

    override val worldSize: WorldSize = WorldSize(200, 200)

    private val camera = Camera3D.perspective(
        45,
        worldSize.ratio,
        near = 1f,
        far = 200f
    )

    private val shader = DefaultShaders.create3d()

    private val player = Player()

    override fun create() {
        camera.translate(0f, 0f, -50f)
        player.setTranslate(-10f, -10f, 0f)
    }

    override fun render(delta: Seconds) {
        if (inputs.isKeyPressed(Key.SPACE)) {
            player.jump(delta)
        }

        player.update(delta)

        shader.render {
            clear(178 / 255f, 235 / 255f, 242 / 255f)
            camera.draw(it)
            player.draw(it)
        }
    }
}
