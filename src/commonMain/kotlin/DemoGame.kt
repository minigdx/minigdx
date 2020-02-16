import threed.GL
import threed.Game
import threed.Seconds
import threed.gl

class DemoGame : Game {

    override fun render(delta: Seconds) {
        gl.clearColor(0, 0, 0, 1)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)
    }
}
