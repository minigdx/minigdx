package threed.entity

import threed.shaders.ShaderProgram

interface Drawable {

    fun draw(program: ShaderProgram)
}
