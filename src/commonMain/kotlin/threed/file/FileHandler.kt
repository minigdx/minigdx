package threed.file

interface Content<T> {

    fun onLoaded(block: (T) -> Unit)
}

expect class FileHander {

    fun read(fileName: String): Content<String>
}
