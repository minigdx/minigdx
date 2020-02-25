package threed.file

interface Content<T> {

    fun onLoaded(block: (T) -> Unit)
}

expect class FileHandler {

    fun read(fileName: String): Content<String>

    fun readData(filename: String): Content<ByteArray>
}
