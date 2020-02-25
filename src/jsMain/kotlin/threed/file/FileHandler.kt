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

    actual fun read(fileName: String): Content<String> {
        val jsonFile = XMLHttpRequest()
        jsonFile.open("GET", window.location.href + fileName, true)

        val content = AsyncContent<String>()

        jsonFile.onreadystatechange = { evt ->
            if (jsonFile.readyState == 4.toShort() && jsonFile.status == 200.toShort()) {
                content.loaded(jsonFile.responseText)
            }
        }

        jsonFile.send()
        return content
    }

    @ExperimentalStdlibApi
    actual fun readData(filename: String): Content<ByteArray> {
        val jsonFile = XMLHttpRequest()
        jsonFile.open("GET", window.location.href + filename, true)

        val content = AsyncContent<ByteArray>()

        jsonFile.onreadystatechange = { evt ->
            if (jsonFile.readyState == 4.toShort() && jsonFile.status == 200.toShort()) {
                content.loaded(jsonFile.responseText.encodeToByteArray())
            }
        }

        jsonFile.send()
        return content
    }
}
