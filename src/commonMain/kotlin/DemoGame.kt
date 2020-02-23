import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.transpose
import threed.GL
import threed.Game
import threed.Seconds
import threed.entity.Camera
import threed.file.MeshReader
import threed.fileHandler
import threed.gl
import threed.graphics.Render
import threed.input.Key
import threed.inputs
import threed.math.RAD2DEG
import threed.shaders.DefaultShaders
import threed.shaders.ShaderUtils

class DemoGame : Game {

    private val camera = Camera.create(45, gl.canvas.width / gl.canvas.height, 0.1, 100)
    private val program = ShaderUtils.createShaderProgram(DefaultShaders.vertexShader, DefaultShaders.fragmentShader)

    lateinit var monkey: Render
    lateinit var cube: Render
    var ready = 2

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
        camera.rotate(-90, 0, 0)

        fileHandler.read("monkey_color2.3d").onLoaded {
            monkey = Render(MeshReader.fromString(it).first())
            ready--
        }

        fileHandler.read("monkey_color2.3d").onLoaded {
            cube = Render(MeshReader.fromString(it).first())
            cube.mesh.translate(4f, 4f, 0f)
            ready--
        }
    }

    var rotation = 0f

    override fun render(delta: Seconds) {
        // --- act ---

        // Camera move
        if (inputs.isKey(Key.A)) {
            camera.translate(0f, 2f * delta, 0f)
        } else if (inputs.isKey(Key.Z)) {
            camera.translate(0f, -2f * delta, 0f)
        }

        if (inputs.isKey(Key.E)) {
            camera.rotateZ(delta * 25f)
        } else if (inputs.isKey(Key.R)) {
            camera.rotateZ(delta * -25f)
        }

        // Cube move
        if (inputs.isKey(Key.O)) {
            cube.mesh.rotateY(delta * 25f)
        } else if (inputs.isKey(Key.L)) {
            cube.mesh.rotateY(delta * -25f)
        }

        if (inputs.isKey(Key.K)) {
            cube.mesh.translate(-2f * delta, 0f, 0f)
        } else if (inputs.isKey(Key.M)) {
            cube.mesh.translate(2f * delta, 0f, 0f)
        }
        // Monkey move

        // rotation += delta
        camera.setRotationX(rotation * RAD2DEG)

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

        if (ready == 0) {
            monkey.draw(program)
            cube.draw(program)
        }
    }
}
