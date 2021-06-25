package com.github.dwursteisen.minigdx.file

import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
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

class FileHandlerCommon(
    private val platformFileHandler: PlatformFileHandler,
    private val logger: Logger,
    private val loaders: Map<KClass<*>, FileLoader<*>> = createLoaders()
) : FileHandler {

    private val assets = mutableMapOf<String, Content<*>>()

    override fun <T> create(filename: String, value: T): Content<T> {
        val content  = Content<T>(filename, logger)
        assets.put(filename, content)
        content.load(value)
        return content
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    override fun <T, R : Any> get(filename: String, rClazz: KClass<R>, map: (R) -> Content<T>): Content<T> {
        return get(filename, rClazz).map(map) as Content<T>
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    override fun <T : Any> get(filename: String, clazz: KClass<T>): Content<T> {
        return assets.getOrPut(filename) { load(filename, clazz) } as Content<T>
    }

    override fun read(filename: String): Content<String> = platformFileHandler.read(filename)

    override fun readData(filename: String): Content<ByteArray> = platformFileHandler.readData(filename)

    override fun readTextureImage(filename: String): Content<TextureImage> = platformFileHandler.readTextureImage(filename)

    override fun readSound(filename: String): Content<Sound> = platformFileHandler.readSound(filename)

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

    override fun isFullyLoaded(): Boolean {
        return assets.all { it.value.loaded() }
    }

    override fun loadingProgress(): Percent {
        val loaded = assets.count { it.value.loaded() }
        return loaded / assets.count().toFloat()
    }
}
