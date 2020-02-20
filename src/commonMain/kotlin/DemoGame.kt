import threed.*
import threed.entity.Camera
import threed.file.MeshReader
import threed.graphics.Render
import threed.math.RAD2DEG
import threed.math.Vector3
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

        program.createUniform("uProjectionMatrix")
        program.createUniform("uModelViewMatrix")

        camera.translate(0, 0, -6)

        fileHandler.read("monkey.3d").onLoaded {
            monkey = Render(MeshReader.fromString(it).first())
            ready--
        }


        fileHandler.read("monkey_color2.3d").onLoaded {
            cube = Render(MeshReader.fromString(it).first())
            ready--
        }
    }

    var rotation = 0f

    override fun render(delta: Seconds) {
        // --- act ---
        rotation = delta

        val modelMatrix = camera.modelMatrix
            .rotate(Vector3.X, 2 * rotation * RAD2DEG)
            .rotate(Vector3.Z, rotation * RAD2DEG)

        // --- draw ---
        // clear
        gl.clearColor(0, 0, 0, 1)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

        gl.useProgram(program)

        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, camera.projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uModelViewMatrix"), false, modelMatrix)

        if (ready == 0) {
            //monkey.draw(program)
            cube.draw(program)
        }
    }
}
