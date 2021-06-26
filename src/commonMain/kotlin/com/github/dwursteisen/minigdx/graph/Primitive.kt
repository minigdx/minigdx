package com.github.dwursteisen.minigdx.graph

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.file.Asset
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.DataSource

class Primitive constructor(
    val texture: Texture,
    var vertices: C3f = floatArrayOf(),
    var normals: C3f = floatArrayOf(),
    var uvs: C2f = floatArrayOf(),
    var weights: C4f = floatArrayOf(),
    var joints: C4f = floatArrayOf(),
    var verticesOrder: ShortArray = shortArrayOf()
) : Asset {

    var verticesBuffer: Buffer? = null
    var normalsBuffer: Buffer? = null
    var uvsBuffer: Buffer? = null
    var verticesOrderBuffer: Buffer? = null
    var jointsBuffer: Buffer? = null
    var weightsBuffer: Buffer? = null

    override fun load(gameContext: GameContext) {
        // TODO: add a check that normals size == vertex size, etc
        val gl = gameContext.gl
        // Push the model
        verticesBuffer = verticesBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, verticesBuffer!!)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = DataSource.FloatDataSource(vertices),
            usage = GL.STATIC_DRAW
        )

        normalsBuffer = normalsBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, normalsBuffer!!)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = DataSource.FloatDataSource(normals),
            usage = GL.STATIC_DRAW
        )

        verticesOrderBuffer = verticesOrderBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrderBuffer!!)
        gl.bufferData(
            target = GL.ELEMENT_ARRAY_BUFFER,
            data = DataSource.ShortDataSource(verticesOrder),
            usage = GL.STATIC_DRAW
        )

        // Push UV coordinates
        uvsBuffer = uvsBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, uvsBuffer!!)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = DataSource.FloatDataSource(uvs),
            usage = GL.STATIC_DRAW
        )

        if (weights.isNotEmpty()) {
            weightsBuffer = weightsBuffer ?: gl.createBuffer()
            gl.bindBuffer(GL.ARRAY_BUFFER, weightsBuffer!!)
            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = DataSource.FloatDataSource(weights),
                usage = GL.STATIC_DRAW
            )
        }

        if (joints.isNotEmpty()) {
            jointsBuffer = jointsBuffer ?: gl.createBuffer()
            gl.bindBuffer(GL.ARRAY_BUFFER, jointsBuffer!!)
            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = DataSource.FloatDataSource(joints),
                usage = GL.STATIC_DRAW
            )
        }
    }
}
