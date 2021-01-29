package com.github.dwursteisen.minigdx.file

import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.reflect.KClass

private fun createLoaders(): Map<KClass<*>, FileLoader<*>> = mapOf(
    TextureImage::class to TextureImageLoader(),
    Texture::class to TextureLoader(),
    AngelCode::class to AngelCodeLoader(),
    Font::class to FontLoader(),
    Scene::class to SceneLoader(),
    Sound::class to SoundLoader()
)

class FileHandler(val platformFileHandler: PlatformFileHandler, val logger: Logger, val loaders: Map<KClass<*>, FileLoader<*>> = createLoaders()) {

    private val assets = mutableMapOf<String, Content<*>>()

    @ExperimentalStdlibApi
    inline fun <reified T : Any> get(filename: String): Content<T> = get(filename, T::class)

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
    private fun <R : Any> load(filename: String, clazz: KClass<R>): Content<R> {
        logger.info("FILE_HANDLER") { "Loading '$filename' as '${clazz.simpleName}' type" }
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

    fun loadingProgress(): Percent {
        val loaded = assets.count { it.value.loaded() }
        return loaded / assets.count().toFloat()
    }
}
