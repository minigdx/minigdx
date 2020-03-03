package com.github.dwursteisen.minigdx

import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.transpose
import com.github.dwursteisen.minigdx.entity.Camera
import com.github.dwursteisen.minigdx.entity.Landmark
import com.github.dwursteisen.minigdx.file.MeshReader
import com.github.dwursteisen.minigdx.graphics.Render
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.shaders.DefaultShaders
import com.github.dwursteisen.minigdx.shaders.ShaderUtils

class DemoGame : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera.create(45, worldSize.ratio, 0.1, 100)
    private val program = ShaderUtils.createShaderProgram(DefaultShaders.vertexShader, DefaultShaders.fragmentShader)

    // lateinit var monkey: Render
    lateinit var cube: Render
    // val armatures: MutableList<CanDraw> = mutableListOf()
    val landmark = Landmark.of()

    var ready = false

    @ExperimentalStdlibApi
    override fun create() {
        program.createAttrib("aVertexPosition")
        program.createAttrib("aVertexColor")
        program.createAttrib("aNormal")

        // Model View Project Matrix
        program.createUniform("uModelMatrix")
        program.createUniform("uViewMatrix")
        program.createUniform("uProjectionMatrix")

        program.createUniform("uNormalMatrix")

        camera.translate(0, 0, -6)
        // camera.rotate(-90, 0, 0)

        fileHandler.readData("monkey_color2.protobuf").onLoaded {
            cube = Render(MeshReader.fromProtobuf(it).first)
            ready = true
        }
    }

    private fun moveCamera(delta: Seconds) {
        camera.translate(0f, 2f * delta, 0f)
    }

    private fun moveCamera2(delta: Seconds) {
        camera.translate(0f, -2f * delta, 0f)
    }

    private fun rotateCamera(delta: Seconds) {
        camera.rotateX(25f * delta)
    }

    private fun moveCube(delta: Seconds) {
        cube.mesh.translate(0f, -2f * delta, 0f)
    }

    private fun moveCube2(delta: Seconds) {
        cube.mesh.translate(0f, 2f * delta, 0f)
    }

    private fun rotateCube(delta: Seconds) {
        cube.mesh.rotateX(25f * delta)
    }

    var index = 0

    var timer = 0f

    lateinit var action: (Seconds) -> Unit

    val actions = listOf(
        this::rotateCube,
        this::moveCamera,
        this::moveCube,
        this::moveCamera2,
        this::moveCube2
    )

    var rotationStart: Float? = null
    var currentRotation: Float = 0f
    var xStart = 0f
    override fun render(delta: Seconds) {
        if (!ready) {
            return
        }

        // --- act ---

        timer -= delta
        if (timer <= 0f) {
            timer = 4f
            action = actions[index]
            index = (index + 1) % actions.size
        }

        val cursor = inputs.isTouched(TouchSignal.TOUCH1)
        if (cursor != null) {
            if (rotationStart == null) {
                rotationStart = cube.mesh.rotation.x
                xStart = cursor.x
            }

            val screenWidth = gl.screen.width
            val factor = (cursor.x - screenWidth * 0.5f) / screenWidth
            currentRotation = factor * 180f
            rotationStart?.run {
                cube.mesh.setRotationX(this + currentRotation)
            }
        } else {
            rotationStart = null
            cube.mesh.rotateX(delta * 10)
        }
        if(inputs.isTouched(TouchSignal.TOUCH2) != null) {
            TODO("Second touch support is working!")
        }

        if (inputs.isKey(Key.U)) {
            camera.translate(0, delta, 0)
        } else if (inputs.isKey(Key.J)) {
            camera.translate(0, -delta, 0)
        }

        if (inputs.isKey(Key.H)) {
            camera.translate(delta, 0, 0)
        } else if (inputs.isKey(Key.K)) {
            camera.translate(-delta, 0, 0)
        }

        if (inputs.isKey(Key.Y)) {
            camera.translate(0, 0, delta)
        } else if (inputs.isKey(Key.I)) {
            camera.translate(0, 0, -delta)
        }

        val modelMatrix = camera.modelMatrix
        val normalMatrix = transpose(inverse(modelMatrix))
        // --- draw ---
        // clear
        gl.clearColor(0, 0, 0, 1)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

        gl.useProgram(program)

        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, camera.projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uViewMatrix"), false, camera.modelMatrix)
        gl.uniformMatrix4fv(program.getUniform("uNormalMatrix"), false, normalMatrix)

        // monkey.draw(program)
        cube.draw(program)
        // landmark.draw(program)
    }
}
