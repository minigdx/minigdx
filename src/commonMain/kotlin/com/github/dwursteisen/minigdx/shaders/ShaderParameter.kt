package com.github.dwursteisen.minigdx.shaders

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.gl
import kotlin.jvm.JvmName

sealed class ShaderParameter(val name: String) {
    abstract fun create(program: ShaderProgram)

    class UniformMat4(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createUniform(name)
        }

        fun apply(program: ShaderProgram, matrix: Mat4) {
            gl.uniformMatrix4fv(program.getUniform(name), false, matrix)
        }
    }

    class UniformArrayMat4(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createUniform(name)
        }

        @JvmName("applyArray")
        fun apply(program: ShaderProgram, matrix: Array<Mat4>) = apply(program, *matrix)

        fun apply(program: ShaderProgram, matrix: List<Mat4>) = apply(program, matrix.toTypedArray())

        fun apply(program: ShaderProgram, vararg matrix: Mat4) {
            val tmpMatrix = Array(matrix.size * 16) { 0f }

            // Copy all matrix values, aligned
            matrix.forEachIndexed { x, mat ->
                val values = mat.asGLArray()
                (0 until 16).forEach { y ->
                    tmpMatrix[x * 16 + y] = values[y]
                }
            }
            gl.uniformMatrix4fv(program.getUniform(name), false, tmpMatrix)
        }
    }

    class UniformInt(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createUniform(name)
        }

        fun apply(program: ShaderProgram, vararg value: Int) {
            when (value.size) {
                0 -> throw IllegalArgumentException("At least one int is expected")
                1 -> gl.uniform1i(program.getUniform(name), value[0])
                2 -> gl.uniform2i(program.getUniform(name), value[0], value[1])
                3 -> gl.uniform3i(program.getUniform(name), value[0], value[1], value[2])
            }
        }
    }

    class UniformVec3(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createUniform(name)
        }
    }

    class AttributeVec2(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createAttrib(name)
        }

        fun apply(program: ShaderProgram, source: Buffer) {
            gl.bindBuffer(GL.ARRAY_BUFFER, source)
            gl.vertexAttribPointer(
                index = program.getAttrib(name),
                size = 2,
                type = GL.FLOAT,
                normalized = false,
                stride = 0,
                offset = 0
            )
            gl.enableVertexAttribArray(program.getAttrib(name))
        }
    }

    class AttributeVec3(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createAttrib(name)
        }

        fun apply(program: ShaderProgram, source: Buffer) {
            gl.bindBuffer(GL.ARRAY_BUFFER, source)
            gl.vertexAttribPointer(
                index = program.getAttrib(name),
                size = 3,
                type = GL.FLOAT,
                normalized = false,
                stride = 0,
                offset = 0
            )
            gl.enableVertexAttribArray(program.getAttrib(name))
        }
    }

    class AttributeVec4(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createAttrib(name)
        }

        fun apply(program: ShaderProgram, source: Buffer) {
            gl.bindBuffer(GL.ARRAY_BUFFER, source)
            gl.vertexAttribPointer(
                index = program.getAttrib(name),
                size = 4,
                type = GL.FLOAT,
                normalized = false,
                stride = 0,
                offset = 0
            )
            gl.enableVertexAttribArray(program.getAttrib(name))
        }
    }

    class UniformSample2D(name: String) : ShaderParameter(name) {
        override fun create(program: ShaderProgram) {
            program.createUniform(name)
        }

        fun apply(program: ShaderProgram, texture: TextureReference, unit: Int = 0) {
            gl.activeTexture(GL.TEXTURE0 + unit)
            gl.bindTexture(GL.TEXTURE_2D, texture)
            gl.uniform1i(program.getUniform(name), unit)
        }
    }
}
