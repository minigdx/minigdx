package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.CanTouchByHitBox
import com.github.dwursteisen.minigdx.entity.HitBox
import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.entity.behavior.JumpBehavior
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.delegate.TouchByHitBox
import com.github.dwursteisen.minigdx.entity.models.Camera2D
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Light
import com.github.dwursteisen.minigdx.entity.text.Text
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

interface DemoGameLifeCycle {

    fun stop()

    fun reset()
}

class Player(private val model: AnimatedModel) :
    DemoGameLifeCycle,
    CanMove by model,
    CanDraw by model,
    CanTouchByHitBox by TouchByHitBox(HitBox(2f, 3f), model) {

    private val jumpingCharge = JumpBehavior(
        charge = 0.8f,
        gravity = -2f,
        currentPosition = { position.y },
        groundPosition = { -10f },
        isJumping = { inputs.isKeyPressed(Key.SPACE) || inputs.isTouched(TouchSignal.TOUCH1) != null }
    )

    var touched: Boolean = false
        private set

    val obstacles: MutableList<Obstacle> = mutableListOf()

    fun update(delta: Seconds) {
        if (touched) return

        model.update(delta)
        jumpingCharge.update(delta)
        if (jumpingCharge.grounded) {
            setTranslate(y = -10f)
        } else {
            translate(y = jumpingCharge.dy)
        }

        obstacles.forEach {
            if (it.hit(this)) {
                touched = true
            }
        }
    }

    override fun stop() = Unit

    override fun reset() {
        touched = false
    }
}

class Obstacle(private val index: Int, private val model: Drawable) :
    DemoGameLifeCycle,
    CanDraw by model,
    CanMove by model,
    CanTouchByHitBox by TouchByHitBox(HitBox(2f, 2f), model) {

    private var stop: Boolean = false

    fun update(delta: Seconds) {
        if (stop) return

        translate(-speed * delta)

        // off the screen
        if (position.x <= -20f) {
            setTranslate(x = 20f, y = -11f, z = 0f)
        }
    }

    override fun stop() {
        stop = true
    }

    override fun reset() {
        stop = false
        setTranslate(
            // put it offscreen
            y = 20f + index * 20f,
            x = -10f,
            z = 10f
        )
    }

    companion object {

        private var speed = 12f
    }
}

class Score(val text: Text) :
    DemoGameLifeCycle,
    CanDraw by text,
    CanMove by text {

    private var time: Int = 0

    private var blinkTime: Float = 0f

    var blink = false

    fun update(delta: Seconds) {
        if (!blink) {
            time += (delta * 100).toInt()

            text.text = asTimeString(time)
        } else {
            blinkTime += delta
            if (blinkTime > BLINK_DURATION + BLINK_COOLDOWN) {
                blinkTime = 0f // reset
            } else if (blinkTime > BLINK_DURATION) {
                text.text = ""
            } else {
                text.text = asTimeString(time)
            }
        }
    }

    private fun asTimeString(t: Int): String {
        val seconds = (t / 100f).toInt()
        val millis = t - (seconds * 100)
        val m = if (millis < 10) {
            "0$millis"
        } else {
            "$millis"
        }
        return "$seconds:$m"
    }

    override fun stop() {
        blink = true
    }

    override fun reset() {
        blinkTime = 0f
        blink = false
        time = 0
    }

    companion object {
        private const val BLINK_DURATION = 1f
        private const val BLINK_COOLDOWN = 0.25f
    }
}

@ExperimentalStdlibApi
class DemoGame : Game {

    override val worldResolution: WorldResolution = WorldResolution(512, 512)

    private val camera = Camera3D.perspective(
        45,
        worldResolution.ratio,
        near = 1f,
        far = 200f
    )

    private val cameraGUI = Camera2D.orthographic(worldResolution)

    private val shader = DefaultShaders.create3d()

    private val gui = DefaultShaders.create2d()

    private val obstacles by fileHandler.get("cactus.protobuf", Drawable::class).map {
        (0 until 1).map { index ->
            Obstacle(index, it.copy())
        }
    }

    private val background by fileHandler.get<Drawable>("montains.protobuf")

    private val score by fileHandler.get("pt_font", Text::class).map { Score(it) }

    private val player by fileHandler.get("dino.protobuf", AnimatedModel::class).map {
        Player(model = it)
    }

    private val entities: MutableList<DemoGameLifeCycle> = mutableListOf()

    private val light: Light = Light()

    override fun create() {
        camera.translate(0f, 0f, -50f)
        player.setTranslate(-10f, -10f, 0f)
        player.obstacles.addAll(obstacles)
        background
            .translate(y = -20f, z = 10f)
            .scale(Vector3(10f, 10f, 10f))

        score.text.text = "00:00"
        score.translate(x = 20f, y = 5f)

        entities.addAll(obstacles)
        entities.add(score)
        entities.add(player)

        entities.forEach { it.reset() }
    }

    override fun render(delta: Seconds) {
        // -- act --
        player.update(delta)
        obstacles.forEach { it.update(delta) }
        score.update(delta)

        if (player.touched) {
            entities.forEach { it.stop() }

            if (inputs.isKeyJustPressed(Key.SPACE) ||
                inputs.isJustTouched(TouchSignal.TOUCH1) != null) {
                entities.forEach { it.reset() }
            }
        } else {
            background.rotateY(5f * delta)
        }

        // -- draw --
        shader.render { shader ->
            clear(178 / 255f, 235 / 255f, 242 / 255f)
            camera.draw(shader)
            light.draw(shader)
            background.draw(shader)
            player.draw(shader)

            obstacles.forEach { it.draw(shader) }
        }

        gui.render { shader ->
            cameraGUI.draw(shader)
            score.draw(shader)
        }
    }
}
