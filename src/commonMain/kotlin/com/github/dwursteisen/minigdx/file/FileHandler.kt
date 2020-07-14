package com.github.dwursteisen.minigdx.file

import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.entity.CanCopy
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.entity.text.AngelCode
import com.github.dwursteisen.minigdx.entity.text.Font
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface FileLoader<T> {

    fun load(filename: String, handler: FileHandler): Content<T>
}

class UnsupportedTypeException(val type: KClass<*>) : RuntimeException("Unsupported type '${type::class}'")

class EarlyAccessException(val filename: String, val property: String) :
    RuntimeException("Content of file '$filename' accessed before being loaded by the property '$property'!")

open class Content<R>(val filename: String) {

    private var isLoaded: Boolean = false

    private var content: R? = null
    private var onLoaded: List<(R) -> Unit> = emptyList()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): R {
        if (isLoaded) {
            return content!!
        } else {
            throw EarlyAccessException(filename, property.name)
        }
    }

    open fun load(content: R?) {
        this.content = content!!
        isLoaded = true
        onLoaded.forEach { it(content) }
    }

    fun <T> map(block: (R) -> T): Content<T> {
        val result = Content<T>(filename)
        this.onLoaded += {
            result.load(block(it))
        }
        if (isLoaded) {
            onLoaded.forEach { it(content!!) }
        }
        return result
    }

    fun <T> flatMap(block: (R) -> Content<T>): Content<T> {
        val result = Content<T>(filename)
        this.onLoaded += { r ->
            val unit = block(r).map { t -> result.load(t) }
        }
        if (isLoaded) {
            onLoaded.forEach { it(content!!) }
        }
        return result
    }

    fun loaded(): Boolean {
        return isLoaded
    }
}

private fun createLoaders(): Map<KClass<*>, FileLoader<*>> = mapOf(
    TextureImage::class to TextureImageLoader(),
    Texture::class to TextureLoader(),
    AngelCode::class to AngelCodeLoader(),
    Font::class to FontLoader(),
    Scene::class to SceneLoader()
)

class FileHandler(val platformFileHandler: PlatformFileHandler, val loaders: Map<KClass<*>, FileLoader<*>> = createLoaders()) {

    private val assets = mutableMapOf<String, Content<*>>()

    @ExperimentalStdlibApi
    inline fun <reified T : Any> get(filename: String): Content<T> = get(filename, T::class)

    @ExperimentalStdlibApi
    inline fun <reified T> copy(filename: String): Content<T> where T : CanCopy<T> = copy(filename, T::class)

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    fun <T, R : Any> get(filename: String, rClazz: KClass<R>, map: (R) -> Content<T>): Content<T> {
        return get(filename, rClazz).map(map) as Content<T>
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    fun <T : Any> get(filename: String, clazz: KClass<T>): Content<T> {
        return assets.getOrPut(filename) { load(filename, clazz) } as Content<T>
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    fun <T> copy(filename: String, clazz: KClass<T>): Content<T> where T : CanCopy<T>, T : Any {
        val asset: Content<T> = get(filename, clazz)
        return asset.map { it.copy() }
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    private fun <R : Any> load(filename: String, clazz: KClass<R>): Content<R> {
        val loader = loaders[clazz]
        if (loader == null) {
            throw UnsupportedTypeException(clazz)
        } else {
            return loader.load(filename, this) as Content<R>
        }
    }

    fun isFullyLoaded(): Boolean {
        return assets.all { it.value.loaded() }
    }
}
