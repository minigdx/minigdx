import threed.GL
import threed.Game
import threed.Seconds
import threed.entity.Camera
import threed.entity.Cube
import threed.gl
import threed.math.RAD2DEG
import threed.math.Vector3
import threed.shaders.DefaultShaders
import threed.shaders.ShaderUtils

class DemoGame : Game {

    private val camera = Camera.create(45, gl.canvas.width / gl.canvas.height, 0.1, 100)
    private val program = ShaderUtils.createShaderProgram(DefaultShaders.vertexShader, DefaultShaders.fragmentShader)
    private val cube = Cube("hello")

    override fun create() {
        program.createAttrib("aVertexPosition")
        program.createAttrib("aVertexColor")

        program.createUniform("uProjectionMatrix")
        program.createUniform("uModelViewMatrix")

        camera.translate(0, 0, -6)
    }

    var rotation = 0f

    override fun render(delta: Seconds) {
        // --- act ---
        rotation = delta

        val modelMatrix = camera.modelMatrix
            .rotate(Vector3.Z, rotation * RAD2DEG)
            .rotate(Vector3.Y, rotation * RAD2DEG)

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

        cube.draw(program)
    }
}
