package com.github.dwursteisen.minigdx.file

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.shaders.TextureReference

class Texture private constructor(
    val id: Id = Id(),
    var textureImage: TextureImage? = null,
    var textureData: ByteArray,
    var width: Int,
    var height: Int,
    var hasAlpha: Boolean = false
) {
    var textureReference: TextureReference? = null

    constructor(id: Id, source: TextureImage, hasAlpha: Boolean) : this(
        id = id,
        textureImage = source,
        textureData = byteArrayOf(),
        width = source.width,
        height = source.height,
        hasAlpha = hasAlpha
    )

    constructor(id: Id, textureData: ByteArray, width: Int, height: Int, hasAlpha: Boolean) : this(
        id = id,
        textureImage = null,
        textureData = textureData,
        width = width,
        height = height,
        hasAlpha = hasAlpha
    )

    fun buffer(gl: GL) {
        val texture = textureReference ?: gl.createTexture()
        gl.bindTexture(GL.TEXTURE_2D, texture)

        gl.texParameteri(
            GL.TEXTURE_2D,
            GL.TEXTURE_MAG_FILTER,
            GL.NEAREST
        )
        gl.texParameteri(
            GL.TEXTURE_2D,
            GL.TEXTURE_MIN_FILTER,
            GL.NEAREST
        )

        val tx = textureImage
        if(tx != null) {
            gl.texImage2D(
                GL.TEXTURE_2D,
                0,
                GL.RGBA,
                GL.RGBA,
                GL.UNSIGNED_BYTE,
                tx
            )
        } else {
            gl.texImage2D(
                GL.TEXTURE_2D,
                0,
                GL.RGBA,
                GL.RGBA,
                width,
                height,
                GL.UNSIGNED_BYTE,
                textureData
            )
        }
        textureReference = texture
    }
}
