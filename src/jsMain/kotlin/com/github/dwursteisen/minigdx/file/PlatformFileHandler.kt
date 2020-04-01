package com.github.dwursteisen.minigdx.file

import kotlin.browser.window
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType

actual class PlatformFileHandler {

    actual fun read(filename: String): Content<String> {
        return asyncContent(filename) { it.contentToString() }
    }

    @ExperimentalStdlibApi
    actual fun readData(filename: String): Content<ByteArray> {
        return asyncContent(filename) { it }
    }

    // https://youtrack.jetbrains.com/issue/KT-30098
    fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()

    private fun <T> asyncContent(filename: String, enc: (ByteArray) -> T): Content<T> {
        val path = window.location.protocol + "//" + window.location.host + window.location.pathname

        val jsonFile = XMLHttpRequest()
        jsonFile.responseType = XMLHttpRequestResponseType.Companion.ARRAYBUFFER
        jsonFile.open("GET", path + filename, true)

        val content = Content<T>()

        jsonFile.onload = { _ ->
            if (jsonFile.readyState == 4.toShort() && jsonFile.status == 200.toShort()) {
                val element = enc((jsonFile.response as ArrayBuffer).toByteArray())
                content.load(element)
            }
        }

        jsonFile.send()
        return content
    }
}
