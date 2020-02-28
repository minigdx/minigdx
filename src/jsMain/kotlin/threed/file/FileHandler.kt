package threed.file

import kotlin.browser.window
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType

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
        return asyncContent(fileName) { it.contentToString() }
    }

    @ExperimentalStdlibApi
    actual fun readData(filename: String): Content<ByteArray> {
        return asyncContent(filename) { it }
    }

    // https://youtrack.jetbrains.com/issue/KT-30098
    fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()

    private fun <T> asyncContent(filename: String, enc: (ByteArray) -> T): AsyncContent<T> {
        total++
        val jsonFile = XMLHttpRequest()
        jsonFile.responseType = XMLHttpRequestResponseType.Companion.ARRAYBUFFER
        jsonFile.open("GET", window.location.href + filename, true)

        val content = AsyncContent<T>()

        jsonFile.onload = { evt ->
            if (jsonFile.readyState == 4.toShort() && jsonFile.status == 200.toShort()) {
                content.loaded(enc((jsonFile.response as ArrayBuffer).toByteArray()))
                loaded++
            }
        }

        jsonFile.send()
        return content
    }

    actual val isLoaded: Boolean = total == loaded

    actual val loadProgression: Float = loaded / total.toFloat()
}
