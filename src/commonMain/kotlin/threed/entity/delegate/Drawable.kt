package threed.entity.delegate

import threed.entity.CanDraw
import threed.graphics.Render
import threed.shaders.ShaderProgram

class Drawable(val render: Render) : CanDraw {

    override fun draw(shader: ShaderProgram) {
        render.draw(shader)
    }
}
