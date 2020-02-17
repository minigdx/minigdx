import threed.GL
import threed.Game
import threed.Seconds
import threed.buffer.DataSource
import threed.entity.Camera
import threed.gl
import threed.math.RAD2DEG
import threed.math.Vector3
import threed.math.asFloatArray
import threed.math.v2
import threed.shaders.DefaultShaders
import threed.shaders.ShaderUtils

class DemoGame : Game {

    private val camera = Camera.create(45, gl.canvas.width / gl.canvas.height, 0.1, 100)
    private val buffer = gl.createBuffer()
    private val color = gl.createBuffer()
    private val program = ShaderUtils.createShaderProgram(DefaultShaders.vertexShader, DefaultShaders.fragmentShader)

    override fun create() {
        program.createAttrib("aVertexPosition")
        program.createAttrib("aVertexColor")

        program.createUniform("uProjectionMatrix")
        program.createUniform("uModelViewMatrix")

        val positions = arrayOf(
            1.0 v2 1.0,
            -1.0 v2 1.0,
            1.0 v2 -1.0,
            -1.0 v2 -1.0
        )

        val colors = floatArrayOf(
            1.0f, 1.0f, 1.0f, 1.0f, // blanc
            1.0f, 0.0f, 0.0f, 1.0f, // rouge
            0.0f, 1.0f, 0.0f, 1.0f, // vert
            0.0f, 0.0f, 1.0f, 1.0f // bleu
        )

        gl.bindBuffer(GL.ARRAY_BUFFER, buffer)
        gl.bufferData(GL.ARRAY_BUFFER, DataSource.FloatDataSource(positions.asFloatArray()), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, color)
        gl.bufferData(GL.ARRAY_BUFFER, DataSource.FloatDataSource(colors), GL.STATIC_DRAW)

        camera.translate(0, 0, -6)
    }

    var rotation = 0f

    override fun render(delta: Seconds) {
        rotation = delta

        // clear
        gl.clearColor(0, 0, 0, 1)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

        // set buffer to attribute
        gl.bindBuffer(GL.ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(
            index = program.getAttrib("aVertexPosition"),
            size = 2,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(program.getAttrib("aVertexPosition"))

        // set buffer to attribute
        gl.bindBuffer(GL.ARRAY_BUFFER, color)
        gl.vertexAttribPointer(
            index = program.getAttrib("aVertexColor"),
            size = 4,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(program.getAttrib("aVertexColor"))

        gl.useProgram(program)

        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, camera.projectionMatrix)

        gl.uniformMatrix4fv(program.getUniform("uModelViewMatrix"), false,
            camera.modelMatrix.rotate(Vector3.Z, rotation * RAD2DEG)
        )
        gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4)
    }
}
