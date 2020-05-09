package com.github.dwursteisen.minigdx.entity.delegate

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.entity.CanCopy
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.CanMoveAndDraw
import com.github.dwursteisen.minigdx.entity.animations.Armature
import com.github.dwursteisen.minigdx.entity.animations.Joint
import com.github.dwursteisen.minigdx.entity.primitives.DrawType
import com.github.dwursteisen.minigdx.entity.primitives.Mesh
import com.github.dwursteisen.minigdx.entity.primitives.Vertice
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

private fun Array<Vertice>.convertJoints(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 3) {
        val y = it % 3
        val x = (it - y) / 3
        val fl = when (y) {
            0 -> this[x].influence?.joinIds?.a?.toFloat() ?: -1f
            1 -> this[x].influence?.joinIds?.b?.toFloat() ?: -1f
            2 -> this[x].influence?.joinIds?.c?.toFloat() ?: -1f
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
        fl
    })
}

private fun Array<Vertice>.convertWeights(): DataSource.FloatDataSource {
    return convert { it.influence?.weight ?: Vector3.X }
}

/**
 * Render a mesh on a screen.
 */
class Model(
    val mesh: Mesh,
    val pose: Armature? = null
) : CanCopy<Model>, CanMoveAndDraw, CanMove by mesh {

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

    /**
     * Joints
     */
    private val joints = gl.createBuffer()

    private val weights = gl.createBuffer()

    init {
        gl.bindBuffer(GL.ARRAY_BUFFER, vertices)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertPositions(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, colors)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertColors(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, normals)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertNormals(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrder)
        gl.bufferData(GL.ELEMENT_ARRAY_BUFFER, mesh.verticesOrder.convertOrder(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, joints)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertJoints(), GL.STATIC_DRAW)

        gl.bindBuffer(GL.ARRAY_BUFFER, weights)
        gl.bufferData(GL.ARRAY_BUFFER, mesh.vertices.convertWeights(), GL.STATIC_DRAW)
    }

    private val jointsCache = generateCache()
    private val tmpMatrix = Array(jointsCache.size * 16) { 0f }

    // Create List of joint ordered by index.
    private fun generateCache(): Array<Joint> {
        return if (pose == null) {
            emptyArray()
        } else {
            Array(pose.allJoints.size) {
                pose[it]
            }
        }
    }

    override fun draw(shader: ShaderProgram) {

        // Set the model matrix
        gl.uniformMatrix4fv(shader.getUniform("uModelMatrix"), false, mesh.modelMatrix)

        if (this.pose != null) {
            // Set if an armature is present
            gl.uniform1i(shader.getUniform("uArmature"), 1)

            // Copy all matrix values, aligned
            jointsCache.forEachIndexed { x, joint ->
                val values = joint.animationTransformation.toArray()
                (0 until 16).forEach { y ->
                    tmpMatrix[x * 16 + y] = values[y]
                }
            }

            gl.uniformMatrix4fv(shader.getUniform("uJointTransformationMatrix"), false, tmpMatrix)
        } else {
            gl.uniform1i(shader.getUniform("uArmature"), -1)
        }

        // set buffer to attribute
        gl.bindBuffer(GL.ARRAY_BUFFER, vertices)
        gl.vertexAttribPointer(
            index = shader.getAttrib("aVertexPosition"),
            size = 3,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(shader.getAttrib("aVertexPosition"))

        // set buffer to attribute
        gl.bindBuffer(GL.ARRAY_BUFFER, colors)
        gl.vertexAttribPointer(
            index = shader.getAttrib("aVertexColor"),
            size = 4,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(shader.getAttrib("aVertexColor"))

        gl.bindBuffer(GL.ARRAY_BUFFER, normals)
        gl.vertexAttribPointer(
            index = shader.getAttrib("aNormal"),
            size = 3,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(shader.getAttrib("aNormal"))

        gl.bindBuffer(GL.ARRAY_BUFFER, joints)
        gl.vertexAttribPointer(
            index = shader.getAttrib("aJoints"),
            size = 3,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(shader.getAttrib("aJoints"))

        gl.bindBuffer(GL.ARRAY_BUFFER, weights)
        gl.vertexAttribPointer(
            index = shader.getAttrib("aWeights"),
            size = 3,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )
        gl.enableVertexAttribArray(shader.getAttrib("aWeights"))

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrder)
        gl.drawElements(mesh.drawType.glType, mesh.verticesOrder.size, GL.UNSIGNED_SHORT, 0)
    }

    override fun copy() = Model(
        mesh = mesh.copy(),
        pose = this.pose
    )
}

private val mappingType = mapOf(
    DrawType.TRIANGLE to GL.TRIANGLES,
    DrawType.LINE to GL.LINES
)
private val DrawType.glType: Int
    get() {
        return mappingType.getValue(this)
    }
