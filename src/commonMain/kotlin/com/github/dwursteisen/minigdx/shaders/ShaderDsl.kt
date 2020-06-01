package com.github.dwursteisen.minigdx.shaders

import com.github.dwursteisen.minigdx.shaders.Parameter.Mat4
import com.github.dwursteisen.minigdx.shaders.Parameter.Vec2
import com.github.dwursteisen.minigdx.shaders.Parameter.Vec3
import com.github.dwursteisen.minigdx.shaders.Parameter.Vec4

interface VertexShader
interface FragmentShader

fun vertex(generator: VertexBuilder.() -> Unit): VertexShader {
    val builder = VertexBuilder()
    builder.generator()
    return builder
}

fun fragment(generator: FragmentBuilder.() -> Unit): FragmentShader {
    val builder = FragmentBuilder()
    builder.generator()
    return builder
}

sealed class Parameter(val name: String, val type: String) {

    class Vec2(name: String) : Parameter(name, "vec2")
    class Vec3(name: String) : Parameter(name, "vec3") {

        operator fun plus(other: Vec3): Vec3 {
            return Vec3(name + " + " + other.name)
        }
    }

    class Vec4(name: String) : Parameter(name, "vec4")
    class Mat4(name: String) : Parameter(name, "mat4")
}

class ShaderParameterBuilder {
    fun vec2(name: String): Vec2 {
        return Vec2(name)
    }

    fun vec3(name: String): Vec3 {
        return Vec3(name)
    }

    fun vec4(name: String): Vec4 {
        return Vec4(name)
    }

    fun mat4(name: String): Mat4 {
        return Mat4(name)
    }
}

abstract class ShaderBuilder {

    private var attributes = emptyList<Parameter>()
    private var uniforms = emptyList<Parameter>()

    fun <T : Parameter> attribute(configuration: ShaderParameterBuilder.() -> T): T {
        val parameter = ShaderParameterBuilder().configuration()
        attributes = attributes + parameter
        return parameter
    }

    fun <T : Parameter> uniform(configuration: ShaderParameterBuilder.() -> T): T {
        val parameter = ShaderParameterBuilder().configuration()
        uniforms = uniforms + parameter
        return parameter
    }

    private fun emit(type: String, parameters: List<Parameter>): String {
        if (parameters.isEmpty()) return ""

        return parameters.sortedBy { it.name }.joinToString("\n") {
            "$type ${it.type} ${it.name};"
        }
    }

    override fun toString(): String {
        return """
#ifdef GL_ES
    precision highp float;
#endif
        
${emit("uniform", uniforms)}
${emit("attribute", attributes)}
            
void main() {
    ${emitBody()}
}
        """.trimIndent()
    }

    abstract fun emitBody(): String
}

class VertexBuilder : VertexShader, ShaderBuilder() {

    private var glPosition: Parameter? = null

    fun glPosition(code: () -> Parameter) {
        glPosition = code()
    }

    override fun emitBody(): String {
        val builder = StringBuilder()
        glPosition?.run { builder.append("gl_Position = $name;") }
        return builder.toString()
    }
}

class FragmentBuilder : FragmentShader, ShaderBuilder() {

    private var glColor: Parameter? = null

    fun glColor(code: () -> Parameter) {
        glColor = code()
    }

    override fun emitBody(): String {
        val builder = StringBuilder()
        glColor?.run { builder.append("gl_FragColor = $name;") }
        return builder.toString()
    }
}
