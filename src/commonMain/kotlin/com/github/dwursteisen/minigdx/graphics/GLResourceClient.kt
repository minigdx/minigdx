package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.graphics.compilers.AnimatedMeshPrimitiveCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.BoundingBoxCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.GLResourceCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.MeshPrimitiveCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.SpritePrimitiveCompiler
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.reflect.KClass

private val basicCompilers: Map<KClass<out GLResourceComponent>, GLResourceCompiler> = mapOf(
    SpritePrimitive::class to SpritePrimitiveCompiler(),
    MeshPrimitive::class to MeshPrimitiveCompiler(),
    AnimatedMeshPrimitive::class to AnimatedMeshPrimitiveCompiler(),
    BoundingBox::class to BoundingBoxCompiler()
)

class MissingGLResourceCompiler(message: String) : RuntimeException(message)

class GLResourceClient(
    val gl: GL,
    val log: Logger,
    val compilers: Map<KClass<out GLResourceComponent>, GLResourceCompiler> = basicCompilers
) {

    private val cache: MutableMap<String, Iterable<GLResourceComponent>> = mutableMapOf()

    fun <T : GLResourceComponent> compile(name: String, component: T) = compile(name, listOf(component))

    fun <T : GLResourceComponent> compile(name: String, components: Iterable<T>) {
        log.info("GL_RESOURCE") { "Compiling '$name' components" }
        components.filter { component -> component.isDirty }
            .forEach { component ->
                compilers[component::class]?.compile(gl, component) ?: throw MissingGLResourceCompiler("Missing GLResourceCompiler for the type '$${component::class.simpleName}''")
            }

        cache[name] = components
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : GLResourceComponent> get(name: String): Iterable<T> {
        return cache.getValue(name) as Iterable<T>
    }
}
