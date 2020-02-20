package threed.file

import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.window

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

actual class FileHander {
    actual fun read(fileName: String): Content<String> {
        val jsonFile = XMLHttpRequest();
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

}
