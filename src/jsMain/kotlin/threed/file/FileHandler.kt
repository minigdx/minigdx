package threed.file

import kotlin.browser.window
import org.w3c.xhr.XMLHttpRequest

class AsyncContent<T> : Content<T> {

    private var onLoaded: (T) -> Unit = { }
    private var content: T? = null

    fun loaded(content: T) {
        this.content = content
        onLoaded.invoke(content)
    }

    override fun onLoaded(block: (T) -> Unit) {
        onLoaded = block
        content?.run { loaded(this) }
    }
}

actual class FileHandler {

    private var total = 0
    private var loaded = 0

    actual fun read(fileName: String): Content<String> {
        return asyncContent(fileName) { it }
    }

    @ExperimentalStdlibApi
    actual fun readData(filename: String): Content<ByteArray> {
        return asyncContent(filename) {
            it.encodeToByteArray()
        }
    }

    private fun <T> asyncContent(filename: String, enc: (String) -> T): AsyncContent<T> {
        total++
        val jsonFile = XMLHttpRequest()
        jsonFile.open("GET", window.location.href + filename, true)

        val content = AsyncContent<T>()

        jsonFile.onreadystatechange = { evt ->
            if (jsonFile.readyState == 4.toShort() && jsonFile.status == 200.toShort()) {
                content.loaded(enc(jsonFile.responseText))
                loaded++
            }
        }

        jsonFile.send()
        return content
    }

    actual val isLoaded: Boolean = total == loaded

    actual val loadProgression: Float = loaded / total.toFloat()
}
