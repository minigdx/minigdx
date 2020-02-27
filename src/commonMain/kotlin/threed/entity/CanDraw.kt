package threed.entity

import threed.shaders.ShaderProgram

interface CanDraw {
    fun draw(shader: ShaderProgram)
}
