package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.ecs.Entity
import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.ecs.Position
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.AnimatedMeshVertexShader
import com.github.dwursteisen.minigdx.shaders.UVFragmentShader

class AnimatedMeshPrimitiveRenderStage : RenderStage<AnimatedMeshVertexShader, UVFragmentShader>(
    vertex = AnimatedMeshVertexShader(),
    fragment = UVFragmentShader(),
    query = EntityQuery(AnimatedMeshPrimitive::class)
) {

    override fun compile(entity: Entity) {
        entity[AnimatedMeshPrimitive::class]
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

                primitive.weightBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ARRAY_BUFFER, primitive.weightBuffer!!)
                gl.bufferData(
                    target = GL.ARRAY_BUFFER,
                    data = primitive.primitive.vertices.flatMap { it.influences }.map { it.weight }.weightDatasource(),
                    usage = GL.STATIC_DRAW
                )

                primitive.jointBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ARRAY_BUFFER, primitive.jointBuffer!!)
                gl.bufferData(
                    target = GL.ARRAY_BUFFER,
                    data = primitive.primitive.vertices.flatMap { it.influences }.map { it.jointId }.jointDatasource(),
                    usage = GL.STATIC_DRAW
                )

                primitive.isCompiled = true
            }
    }

    private fun List<Float>.weightDatasource(): DataSource.FloatDataSource {
        return DataSource.FloatDataSource(this.toFloatArray())
    }

    private fun List<Int>.jointDatasource(): DataSource.FloatDataSource {
        return DataSource.FloatDataSource(this.map { it.toFloat() }.toFloatArray())
    }

    override fun update(delta: Seconds, entity: Entity) {
        val combined = camera?.let {
            val view = it[Position::class].first().transformation
            val projection = it[Camera::class].first().projection
            projection * view
        } ?: Mat4.identity()
        val model = entity[Position::class].first().transformation
        val animatedModel = entity[AnimatedModel::class].first()

        vertex.uModelView.apply(program, combined * model)
        vertex.uJointTransformationMatrix.apply(program, animatedModel.currentPose)

        entity[AnimatedMeshPrimitive::class].forEach { primitive ->
            vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
            vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
            vertex.aWeights.apply(program, primitive.weightBuffer!!)
            vertex.aJoints.apply(program, primitive.jointBuffer!!)
            fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
            gl.drawElements(
                GL.TRIANGLES, primitive.primitive.verticesOrder.size,
                GL.UNSIGNED_SHORT, 0
            )
        }
    }
}
