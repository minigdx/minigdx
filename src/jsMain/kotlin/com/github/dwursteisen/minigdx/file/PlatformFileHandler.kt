package com.github.dwursteisen.minigdx.file

import kotlin.browser.window
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.Image
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
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

    actual fun readTextureImage(filename: String): Content<TextureImage> {
        val img = Image()
        img.src = computeUrl(filename)

        val content = Content<TextureImage>(filename)

        img.addEventListener("load", object : EventListener {
            override fun handleEvent(event: Event) {
                content.load(
                    TextureImage(
                        source = img,
                        width = img.width,
                        height = img.height
                    )
                )
            }
        })
        return content
    }

    // https://youtrack.jetbrains.com/issue/KT-30098
    fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()

    private fun <T> asyncContent(filename: String, enc: (ByteArray) -> T): Content<T> {
        val url = computeUrl(filename)

        val jsonFile = XMLHttpRequest()
        jsonFile.responseType = XMLHttpRequestResponseType.Companion.ARRAYBUFFER
        jsonFile.open("GET", url, true)

        val content = Content<T>(filename)

        jsonFile.onload = { _ ->
            if (jsonFile.readyState == 4.toShort() && jsonFile.status == 200.toShort()) {
                val element = enc((jsonFile.response as ArrayBuffer).toByteArray())
                content.load(element)
            }
        }

        jsonFile.send()
        return content
    }

    private fun computeUrl(filename: String): String {
        val path = window.location.protocol + "//" + window.location.host + window.location.pathname
        val url = path + filename
        return url
    }
}
