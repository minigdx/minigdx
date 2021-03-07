package com.github.dwursteisen.minigdx.graphics

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.graphics.compilers.AnimatedMeshPrimitiveCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.BoundingBoxCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.GLResourceCompiler
import com.github.dwursteisen.minigdx.graphics.compilers.MeshPrimitiveCompiler
import com.github.dwursteisen.minigdx.logger.Logger
import com.github.dwursteisen.minigdx.shaders.TextureReference
import kotlin.reflect.KClass

private val basicCompilers: Map<KClass<out GLResourceComponent>, GLResourceCompiler> = mapOf(
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

    private val cache: MutableMap<Id, GLResourceComponent> = mutableMapOf()

    private val materials: MutableMap<Id, TextureReference> = mutableMapOf()

    fun <T : GLResourceComponent> compile(component: T) = compile(listOf(component))

    fun <T : GLResourceComponent> compile(components: Iterable<T>) {
        components
            .filter { it.isDirty }
            .onEach { component ->
                val cachedValue = cache[component.id]
                if (cachedValue != null) {
                    compilers[component::class]?.synchronize(gl, cachedValue, component, materials)
                } else {
                    log.info("GL_RESOURCE") { "Compiling '${component.id}' (${component::class.simpleName}) component" }
                    cache[component.id] = component
                    compilers[component::class]?.compile(gl, component, materials)
                        ?: throw MissingGLResourceCompiler("Missing GLResourceCompiler for the type '${component::class.simpleName}'")
                }
            }.forEach {
                it.isDirty = false
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : GLResourceComponent> get(name: Id): Iterable<T> {
        return cache.getValue(name) as Iterable<T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : GLResourceComponent> getOrCreate(name: Id, factory: () -> T): T {
        val component = cache[name]
        return if (component == null) {
            val newComponent = factory()
            compile(newComponent)
            newComponent
        } else {
            component as T
        }
    }
}
