import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.transpose
import threed.*
import threed.entity.Camera
import threed.file.MeshReader
import threed.graphics.Render
import threed.input.Key
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

        program.createUniform("uProjectionMatrix")
        program.createUniform("uModelViewMatrix")
        program.createUniform("uNormalMatrix")

        camera.translate(0, 0, -6)
        camera.rotate(-90, 0, 0)

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

        /*
        if(inp.isKey(Key.A)) {
            rotation += delta
        } else if(inp.isKey(Key.Z)) {
            rotation -= delta
        }
        */
        rotation += delta
        
        val modelMatrix = camera.modelMatrix * rotation(Float3(0f, 0f, 1f), rotation * RAD2DEG)
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
        gl.uniformMatrix4fv(program.getUniform("uModelViewMatrix"), false, modelMatrix)
        gl.uniformMatrix4fv(program.getUniform("uNormalMatrix"), false, normalMatrix)

        if (ready == 0) {
            //monkey.draw(program)
            cube.draw(program)
        }
    }
}
