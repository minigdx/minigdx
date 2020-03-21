package com.github.dwursteisen.minigdx

import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.transpose
import com.github.dwursteisen.minigdx.entity.Camera
import com.github.dwursteisen.minigdx.entity.Landmark
import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.file.MeshReader
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

class DemoAnimation : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera.create(45, worldSize.ratio, 1, 100)

    private val program = DefaultShaders.create()

    private val landmark = Landmark.of()

    private var animatedModel: AnimatedModel? = null

    @ExperimentalStdlibApi
    override fun create() {

        camera.translate(0, 0, -20)

        fileHandler.readData("F.protobuf").onLoaded {
            val fromProtobuf = MeshReader.fromProtobuf(it)
            val (mesh, armature, animations) = fromProtobuf

            animatedModel = AnimatedModel(
                animation = animations!!,
                mesh = mesh,
                armature = armature!!,
                drawJoint = true
            )
        }
    }

    override fun render(delta: Seconds) {
        if (!fileHandler.isLoaded) {
            return
        }

        // --- act ---

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

        animatedModel?.update(delta)

        animatedModel?.draw(program)
        landmark.draw(program)
    }
}
