package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.model.UV
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.MeshVertexShader
import com.github.dwursteisen.minigdx.shaders.UVFragmentShader

fun List<com.dwursteisen.minigdx.scene.api.model.Position>.positionsDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 3) {
        val y = it % 3
        val x = (it - y) / 3
        when (y) {
            0 -> this[x].x
            1 -> this[x].y
            2 -> this[x].z
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

fun List<UV>.uvDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 2) {
        val y = it % 2
        val x = (it - y) / 2
        when (y) {
            0 -> this[x].x
            1 -> this[x].y
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

class MeshPrimitiveRenderStage(gl: GL) : RenderStage<MeshVertexShader, UVFragmentShader>(
    gl = gl,
    vertex = MeshVertexShader(),
    fragment = UVFragmentShader(),
    query = EntityQuery(MeshPrimitive::class)
) {

    override fun compile(entity: Entity) {
        entity.findAll(MeshPrimitive::class)
            .filter { !it.isCompiled }
            .forEach { primitive ->
                // Push the model
                primitive.verticesBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ARRAY_BUFFER, primitive.verticesBuffer!!)

                gl.bufferData(
                    target = GL.ARRAY_BUFFER,
                    data = primitive.primitive.vertices.map { it.position }.positionsDatasource(),
                    usage = GL.STATIC_DRAW
                )

                primitive.verticesOrderBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
                gl.bufferData(
                    target = GL.ELEMENT_ARRAY_BUFFER,
                    data = DataSource.ShortDataSource(primitive.primitive.verticesOrder.map { it.toShort() }
                        .toShortArray()),
                    usage = GL.STATIC_DRAW
                )

                // Push the texture
                val textureReference = gl.createTexture()
                gl.bindTexture(GL.TEXTURE_2D, textureReference)

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
                gl.texImage2D(
                    GL.TEXTURE_2D,
                    0,
                    GL.RGBA,
                    GL.RGBA,
                    primitive.material.width,
                    primitive.material.height,
                    GL.UNSIGNED_BYTE,
                    primitive.material.data
                )

                primitive.textureReference = textureReference

                // Push UV coordinates
                primitive.uvBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ARRAY_BUFFER, primitive.uvBuffer!!)

                gl.bufferData(
                    target = GL.ARRAY_BUFFER,
                    data = primitive.primitive.vertices.map { it.uv }.uvDatasource(),
                    usage = GL.STATIC_DRAW
                )

                primitive.isCompiled = true
            }
    }

    override fun update(delta: Seconds, entity: Entity) {
        val combined = camera?.let {
            val view = it.get(Position::class).transformation
            val projection = it.get(Camera::class).projection
            projection * view
        } ?: Mat4.identity()
        val model = entity.get(Position::class).transformation

        vertex.uModelView.apply(program, combined * model)

        entity.findAll(MeshPrimitive::class).forEach { primitive ->
            vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
            vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
            fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
            gl.drawElements(
                GL.TRIANGLES, primitive.primitive.verticesOrder.size,
                GL.UNSIGNED_SHORT, 0)
        }
    }
}
