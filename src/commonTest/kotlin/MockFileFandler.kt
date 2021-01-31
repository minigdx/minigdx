import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.file.Content
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.Sound
import com.github.dwursteisen.minigdx.file.TextureImage
import kotlin.reflect.KClass

class MockFileFandler : FileHandler {
    override fun <T, R : Any> get(filename: String, rClazz: KClass<R>, map: (R) -> Content<T>): Content<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(filename: String, clazz: KClass<T>): Content<T> {
        TODO("Not yet implemented")
    }

    override fun read(filename: String): Content<String> {
        TODO("Not yet implemented")
    }

    override fun readData(filename: String): Content<ByteArray> {
        TODO("Not yet implemented")
    }

    override fun readTextureImage(filename: String): Content<TextureImage> {
        TODO("Not yet implemented")
    }

    override fun readSound(filename: String): Content<Sound> {
        TODO("Not yet implemented")
    }

    override fun isFullyLoaded(): Boolean = true

    override fun loadingProgress(): Percent = 1.0f
}
