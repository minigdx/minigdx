import threed.GL
import threed.Game
import threed.Seconds
import threed.buffer.DataSource
import threed.entity.Camera
import threed.gl
import threed.math.RAD2DEG
import threed.math.Vector3
import threed.shaders.DefaultShaders
import threed.shaders.ShaderUtils

class DemoGame : Game {

    private val camera = Camera.create(45, gl.canvas.width / gl.canvas.height, 0.1, 100)
    private val buffer = gl.createBuffer()
    private val color = gl.createBuffer()
    private val indices = gl.createBuffer()
    private val program = ShaderUtils.createShaderProgram(DefaultShaders.vertexShader, DefaultShaders.fragmentShader)

    override fun create() {
        program.createAttrib("aVertexPosition")
        program.createAttrib("aVertexColor")

        program.createUniform("uProjectionMatrix")
        program.createUniform("uModelViewMatrix")

        val positions = floatArrayOf(
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 0.5f,
            -1.0f, 1.0f, 0.5f,
            1.0f, -1.0f, 0.5f,
            -1.0f, -1.0f, 0.5f
        )

        val colors = floatArrayOf(
            148f / 255f, 219f / 255f, 128f / 255f, 1.0f, // blanc
            148f / 255f, 219f / 255f, 128f / 255f, 1.0f, // blanc
            148f / 255f, 219f / 255f, 128f / 255f, 1.0f, // blanc
            148f / 255f, 219f / 255f, 128f / 255f, 1.0f, // blanc
            1.0f, 0.0f, 0.0f, 1.0f, // rouge
            1.0f, 0.0f, 0.0f, 1.0f, // rouge
            1.0f, 0.0f, 0.0f, 1.0f, // rouge
            1.0f, 0.0f, 0.0f, 1.0f, // rouge
            0.0f, 1.0f, 0.0f, 1.0f, // vert
            0.0f, 1.0f, 0.0f, 1.0f, // vert
            0.0f, 1.0f, 0.0f, 1.0f, // vert
            0.0f, 1.0f, 0.0f, 1.0f, // vert
            0.0f, 0.0f, 1.0f, 1.0f, // bleu
            0.0f, 0.0f, 1.0f, 1.0f, // bleu
            0.0f, 0.0f, 1.0f, 1.0f, // bleu
            0.0f, 0.0f, 1.0f, 1.0f // bleu
        )

        val indice = shortArrayOf(
            0, 3, 1,
            1, 2, 3,
            4, 7, 5,
            5, 6, 7
        )
        gl.bindBuffer(GL.ARRAY_BUFFER, buffer)
        gl.bufferData(GL.ARRAY_BUFFER, DataSource.FloatDataSource(positions), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, color)
        gl.bufferData(GL.ARRAY_BUFFER, DataSource.FloatDataSource(colors), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indices)
        gl.bufferData(GL.ELEMENT_ARRAY_BUFFER, DataSource.ShortDataSource(indice), GL.STATIC_DRAW)

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
            size = 3,
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

        gl.uniformMatrix4fv(
            program.getUniform("uModelViewMatrix"), false,
            camera.modelMatrix.rotate(Vector3.Z, rotation * RAD2DEG)
                .rotate(Vector3.Y, rotation * RAD2DEG)
        )

        gl.drawElements(GL.TRIANGLES, 12, GL.UNSIGNED_SHORT, 0)
    }
}
