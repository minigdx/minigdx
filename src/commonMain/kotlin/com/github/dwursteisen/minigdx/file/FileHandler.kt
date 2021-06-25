package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.Percent
import kotlin.reflect.KClass

interface FileHandler {

    fun <T> create(filename: String, value: T): Content<T>

    fun <T, R : Any> get(filename: String, rClazz: KClass<R>, map: (R) -> Content<T>): Content<T>

    fun <T : Any> get(filename: String, clazz: KClass<T>): Content<T>

    fun read(filename: String): Content<String>

    fun readData(filename: String): Content<ByteArray>

    fun readTextureImage(filename: String): Content<TextureImage>

    fun readSound(filename: String): Content<Sound>

    fun isFullyLoaded(): Boolean

    fun loadingProgress(): Percent
}

@ExperimentalStdlibApi
inline fun <reified T : Any> FileHandler.get(filename: String): Content<T> = get(filename, T::class)
