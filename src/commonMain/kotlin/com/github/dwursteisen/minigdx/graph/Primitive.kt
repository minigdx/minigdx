package com.github.dwursteisen.minigdx.graph

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.file.Asset
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.DataSource

enum class DrawingType {
    TRIANGLE,
    LINE
}
class Primitive constructor(
    var texture: Texture,
    vertices: C3f = floatArrayOf(),
    normals: C3f = floatArrayOf(),
    uvs: C2f = floatArrayOf(),
    weights: C4f = floatArrayOf(),
    joints: C4f = floatArrayOf(),
    verticesOrder: ShortArray = shortArrayOf(),
    var drawingType: DrawingType = DrawingType.TRIANGLE
) : Asset {

    var verticesBuffer: Buffer? = null
    var normalsBuffer: Buffer? = null
    var uvsBuffer: Buffer? = null
    var verticesOrderBuffer: Buffer? = null
    var jointsBuffer: Buffer? = null
    var weightsBuffer: Buffer? = null

    private var _verticesUpdated: Boolean = true
    var vertices: C3f = vertices
        set(value) {
            _verticesUpdated = true
            field = value
        }

    private var _normalsUpdated: Boolean = true
    var normals: C3f = normals
        set(value) {
            _normalsUpdated = true
            field = value
        }

    private var _uvsUpdated: Boolean = true
    var uvs: C2f = uvs
        set(value) {
            _uvsUpdated = true
            field = value
        }

    private var _verticesOrderUpdated: Boolean = true
    var verticesOrder: ShortArray = verticesOrder
        set(value) {
            _verticesOrderUpdated = true
            field = value
        }

    private var _jointsUpdated: Boolean = true
    var joints: C4f = joints
        set(value) {
            _jointsUpdated = true
            field = value
        }

    private var _weightsUpdated: Boolean = true
    var weights: C4f = weights
        set(value) {
            _weightsUpdated = true
            field = value
        }

    override fun load(gameContext: GameContext) {
        val gl = gameContext.gl

        // Push the model
        verticesBuffer = verticesBuffer ?: gl.createBuffer()
        if (_verticesUpdated) {
            gl.bindBuffer(GL.ARRAY_BUFFER, verticesBuffer!!)
            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = DataSource.FloatDataSource(vertices),
                usage = GL.STATIC_DRAW
            )
            _verticesUpdated = false
        }

        normalsBuffer = normalsBuffer ?: gl.createBuffer()
        if (_normalsUpdated) {
            gl.bindBuffer(GL.ARRAY_BUFFER, normalsBuffer!!)
            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = DataSource.FloatDataSource(normals),
                usage = GL.STATIC_DRAW
            )

            _normalsUpdated = false
        }

        verticesOrderBuffer = verticesOrderBuffer ?: gl.createBuffer()
        if (_verticesOrderUpdated) {
            _verticesOrderUpdated = false
            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrderBuffer!!)
            gl.bufferData(
                target = GL.ELEMENT_ARRAY_BUFFER,
                data = DataSource.ShortDataSource(verticesOrder),
                usage = GL.STATIC_DRAW
            )
        }

        // Push UV coordinates
        uvsBuffer = uvsBuffer ?: gl.createBuffer()
        if (_uvsUpdated) {
            _uvsUpdated = false
            gl.bindBuffer(GL.ARRAY_BUFFER, uvsBuffer!!)
            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = DataSource.FloatDataSource(uvs),
                usage = GL.STATIC_DRAW
            )
        }

        if (_weightsUpdated) {
            _weightsUpdated = false
            if (weights.isNotEmpty()) {
                weightsBuffer = weightsBuffer ?: gl.createBuffer()
                gl.bindBuffer(GL.ARRAY_BUFFER, weightsBuffer!!)
                gl.bufferData(
                    target = GL.ARRAY_BUFFER,
                    data = DataSource.FloatDataSource(weights),
                    usage = GL.STATIC_DRAW
                )
            }
        }

        if (_jointsUpdated) {
            _jointsUpdated = false
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
}
