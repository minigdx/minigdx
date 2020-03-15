package com.github.dwursteisen.minigdx

import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.transpose
import com.github.dwursteisen.minigdx.entity.Camera
import com.github.dwursteisen.minigdx.entity.Landmark
import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.file.MeshReader
import com.github.dwursteisen.minigdx.graphics.Render
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.shaders.DefaultShaders
import com.github.dwursteisen.minigdx.shaders.ShaderUtils

class DemoGame : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera.create(45, worldSize.ratio, 1, 1000)
    private val program = ShaderUtils.createShaderProgram(DefaultShaders.vertexShader, DefaultShaders.fragmentShader)

    lateinit var cube: Render
    private val landmark = Landmark.of()
    private lateinit var animatedModel: AnimatedModel

    @ExperimentalStdlibApi
    override fun create() {

        program.createAttrib("aVertexPosition")
        program.createAttrib("aVertexColor")
        program.createAttrib("aNormal")
        program.createAttrib("aJoints")
        program.createAttrib("aWeights")

        // FIXME: create by default this uniform.
        // Model View Project Matrix
        program.createUniform("uModelMatrix")
        program.createUniform("uViewMatrix")
        program.createUniform("uProjectionMatrix")

        program.createUniform("uNormalMatrix")
        program.createUniform("uArmature")
        // FIXME: https://www.gamedev.net/forums/topic/658191-webgl-how-to-send-an-array-of-matrices-to-the-vertex-shader/
        program.createUniform("uJointTransformationMatrix")

        camera.translate(0, 0, -20)
        // camera.rotate(-90, 0, 0)

        fileHandler.readData("bone_debug.protobuf").onLoaded {
            val fromProtobuf = MeshReader.fromProtobuf(it)
            val (mesh, armature, animations) = fromProtobuf

            animatedModel = AnimatedModel(
                animation = animations!!,
                mesh = mesh,
                armature = armature!!,
                drawJoin = true
            )
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
        if (!fileHandler.isLoaded) {
            return
        }

        // --- act ---

        timer -= delta
        if (timer <= 0f) {
            timer = 4f
            action = actions[index]
            index = (index + 1) % actions.size
        }
/*
        val cursor = inputs.isTouched(TouchSignal.TOUCH1)
        if (cursor != null) {
            if (rotationStart == null) {
                rotationStart = cube.mesh.rotation.y
                xStart = cursor.x
            }

            val screenWidth = gl.screen.width
            val factor = (cursor.x - screenWidth * 0.5f) / screenWidth
            currentRotation = factor * 180f
            rotationStart?.run {
                cube.mesh.setRotationY(this + currentRotation)
            }
        } else {
            rotationStart = null
            cube.mesh.rotateY(delta * 10)
        }
*/
        if (inputs.isKey(Key.U)) {
            camera.translate(0, delta, 0)
        } else if (inputs.isKey(Key.J)) {
            camera.translate(0, -delta, 0)
        }

        if (inputs.isKey(Key.Y)) {
            camera.rotateX(delta)
        } else if (inputs.isKey(Key.I)) {
            camera.rotateX(-delta)
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

        if (inputs.isKeyPressed(Key.F)) {
            animatedModel.animator.nextFrame()
        }
        //  animatedModel.animator.nextFrame()

        val modelMatrix = camera.modelMatrix
        val normalMatrix = transpose(inverse(modelMatrix))
        // --- draw ---
        // TODO: create clear method
        gl.clearColor(1 / 255f, 191 / 255f, 255 / 255f, 1f)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

        // TODO: create program.draw { … }
        gl.useProgram(program)

        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, camera.projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uViewMatrix"), false, camera.modelMatrix)
        gl.uniformMatrix4fv(program.getUniform("uNormalMatrix"), false, normalMatrix)

        animatedModel.update(delta)

        animatedModel.draw(program)
        landmark.draw(program)
    }
}
