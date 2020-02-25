package threed.file

import java.io.File

class SyncContent<T>(val content: T) : Content<T> {

    override fun onLoaded(block: (T) -> Unit) {
        block(content)
    }
}

actual class FileHandler {

    actual fun read(fileName: String): Content<String> {
        val content = File(fileName).readText()
        return SyncContent(content)
    }

    actual fun readData(filename: String): Content<ByteArray> {
        val content = File(filename).readBytes()
        return SyncContent(content)
    }
}
