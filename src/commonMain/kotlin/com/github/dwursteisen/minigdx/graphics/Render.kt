package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.entity.DrawType
import com.github.dwursteisen.minigdx.entity.Mesh
import com.github.dwursteisen.minigdx.entity.Vertice
import com.github.dwursteisen.minigdx.entity.animations.Armature
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

private fun Array<Vertice>.convertNormals(): DataSource.FloatDataSource {
    return convert { it.normal }
}

private fun Array<Vertice>.convert(field: (Vertice) -> Vector3): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 3) {
        val y = it % 3
        val x = (it - y) / 3
        when (y) {
            0 -> field(this[x]).x
            1 -> field(this[x]).y
            2 -> field(this[x]).z
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

private fun Array<Vertice>.convertPositions(): DataSource.FloatDataSource {
    return convert { it.position }
}

private fun Array<Vertice>.convertColors(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 4) {
        val y = it % 4
        val x = (it - y) / 4
        when (y) {
            0 -> this[x].color.r
            1 -> this[x].color.g
            2 -> this[x].color.b
            3 -> this[x].color.alpha
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

private fun ShortArray.convertOrder(): DataSource.ShortDataSource {
    return DataSource.ShortDataSource(this)
}

/**
 * Render a mesh on a screen.
 */
class Render(val mesh: Mesh, var pose: Armature? = null) {

    /**
     * Positions of vertices.
     */
    private val vertices = gl.createBuffer()

    /**
     * Colors of vertices.
     */
    private val colors = gl.createBuffer()

    /**
     * Normals of vertices.
     */
    private val normals = gl.createBuffer()

    /**
     * Order to drawn vertices.
     */
    private val verticesOrder = gl.createBuffer()

    init {
        gl.bindBuffer(GL.ARRAY_BUFFER, vertices)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertPositions(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, colors)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertColors(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, normals)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertNormals(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrder)
        gl.bufferData(GL.ELEMENT_ARRAY_BUFFER, mesh.verticesOrder.convertOrder(), GL.STATIC_DRAW)
    }

    fun draw(program: ShaderProgram) {

        // Set the model matrix
        gl.uniformMatrix4fv(program.getUniform("uModelMatrix"), false, mesh.modelMatrix)

        if (pose != null) {
            // Set if an armature is present
            gl.uniform1i(program.getUniform("uArmature"), 1)

            // Set the joint matrix, if any
            val root = pose!!.rootJoint.globalInverseBindTransformation

            gl.uniformMatrix4fv(program.getUniform("uJointTransformationMatrix"), false, root)
        } else {
            gl.uniform1i(program.getUniform("uArmature"), -1)
        }

        // set buffer to attribute
        gl.bindBuffer(GL.ARRAY_BUFFER, vertices)
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
        gl.bindBuffer(GL.ARRAY_BUFFER, colors)
        gl.vertexAttribPointer(
            index = program.getAttrib("aVertexColor"),
            size = 4,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(program.getAttrib("aVertexColor"))

        gl.bindBuffer(GL.ARRAY_BUFFER, normals)
        gl.vertexAttribPointer(
            index = program.getAttrib("aNormal"),
            size = 3,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(program.getAttrib("aNormal"))

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrder)
        gl.drawElements(mesh.drawType.glType, mesh.verticesOrder.size, GL.UNSIGNED_SHORT, 0)
    }
}

private val mappingType = mapOf(
    DrawType.TRIANGLE to GL.TRIANGLES,
    DrawType.LINE to GL.LINES
)
private val DrawType.glType: Int
    get() {
        return mappingType.getValue(this)
    }
