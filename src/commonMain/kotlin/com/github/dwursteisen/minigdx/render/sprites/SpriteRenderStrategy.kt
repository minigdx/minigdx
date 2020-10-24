package com.github.dwursteisen.minigdx.render.sprites

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity

@Deprecated("To be removed")
object SpriteRenderStrategy : RenderStrategy {
    override fun render(gl: GL, entity: Entity) {
        val spritePrimitive = entity.get(SpritePrimitive::class)

        gl.bindTexture(GL.TEXTURE_2D, spritePrimitive.textureReference!!)
        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, spritePrimitive.verticesOrderBuffer!!)
        gl.drawElements(
            GL.TRIANGLES, spritePrimitive.numberOfIndices,
            GL.UNSIGNED_SHORT, 0
        )
        gl.disable(GL.BLEND)
    }
}
