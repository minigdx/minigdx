package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.reflect.KProperty

open class Content<R>(val filename: String, val logger: Logger) {

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
        logger.info("CONTENT") { "Loaded '$filename' content" }
        onLoaded.forEach { it(content) }
    }

    fun <T> map(block: (R) -> T): Content<T> {
        val result = Content<T>(filename, logger)
        this.onLoaded += {
            result.load(block(it))
        }
        if (isLoaded) {
            onLoaded.forEach { it(content!!) }
        }
        return result
    }

    fun <T> flatMap(block: (R) -> Content<T>): Content<T> {
        val result = Content<T>(filename, logger)
        this.onLoaded += { r ->
            @Suppress("UNUSED_VARIABLE")
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
