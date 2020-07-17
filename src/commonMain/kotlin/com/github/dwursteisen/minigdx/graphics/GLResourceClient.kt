package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.graphics.compilers.AnimatedMeshPrimitiveCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.GLResourceCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.MeshPrimitiveCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.SpritePrimitiveCompiler
import kotlin.reflect.KClass

private val basicCompilers: Map<KClass<out GLResourceComponent>, GLResourceCompiler> = mapOf(
    SpritePrimitive::class to SpritePrimitiveCompiler(),
    MeshPrimitive::class to MeshPrimitiveCompiler(),
    AnimatedMeshPrimitive::class to AnimatedMeshPrimitiveCompiler()
)

class GLResourceClient(
    val gl: GL,
    val compilers: Map<KClass<out GLResourceComponent>, GLResourceCompiler> = basicCompilers
) {

    private val cache: MutableMap<String, Iterable<GLResourceComponent>> = mutableMapOf()

    fun <T : GLResourceComponent> compile(name: String, component: T) = compile(name, listOf(component))

    fun <T : GLResourceComponent> compile(name: String, components: Iterable<T>) {
        components.filter { component -> component.isDirty }
            .forEach { component ->
                compilers.getValue(component::class).compile(gl, component)
            }

        cache[name] = components
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : GLResourceComponent> get(name: String): Iterable<T> {
        return cache.getValue(name) as Iterable<T>
    }
}
