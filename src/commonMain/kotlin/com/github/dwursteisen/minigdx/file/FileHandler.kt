package com.github.dwursteisen.minigdx.file

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface FileLoader<T> {

    @ExperimentalStdlibApi
    fun load(filename: String, content: String): T

    @ExperimentalStdlibApi
    fun load(filename: String, content: ByteArray): T
}

class UnsupportedType(val type: KClass<*>) : RuntimeException("Unsupported type '${type::class}'")

open class Content<R> {

    private val nop: (R) -> Unit = { }

    private var isLoaded: Boolean = false
    private var content: R? = null
    private var onLoaded: (R) -> Unit = nop

    operator fun getValue(thisRef: Any?, property: KProperty<*>): R {
        if (isLoaded) {
            return content!!
        } else {
            throw RuntimeException("Content accessed before being loaded!")
        }
    }

    open fun load(content: R?) {
        this.content = content!!
        isLoaded = true
        onLoaded(content)
    }

    fun <T> map(block: (R) -> T): Content<T> {
        val result = Content<T>()
        this.onLoaded = {
            result.load(block(it))
        }
        if (isLoaded) {
            onLoaded(content!!)
        }
        return result
    }

    fun loaded(): Boolean {
        return isLoaded
    }
}

class FileHandler(val handler: PlatformFileHandler, val loaders: Map<KClass<*>, FileLoader<*>>) {

    private val assets = mutableMapOf<String, Content<*>>()

    @ExperimentalStdlibApi
    inline fun <reified T> get(filename: String): Content<T> {
        return get(filename, T::class)
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    fun <T> get(filename: String, clazz: KClass<*>): Content<T> {
        return assets.getOrPut(filename) { load(filename, clazz) } as Content<T>
    }

    @ExperimentalStdlibApi
    private fun load(filename: String, clazz: KClass<*>): Content<*> {
        val loader = loaders[clazz]
        if (loader == null) {
            throw UnsupportedType(clazz)
        } else {
            val content = handler.readData(filename)
            val asModel = content.map { loader.load(filename, it) }
            assets[filename] = asModel
            return asModel
        }
    }

    fun isFullyLoaded(): Boolean {
        return assets.all { it.value.loaded() }
    }
}
