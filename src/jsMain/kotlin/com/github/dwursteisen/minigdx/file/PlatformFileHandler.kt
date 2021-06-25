package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.audio.AudioContext
import com.github.dwursteisen.minigdx.logger.Logger
import kotlinx.browser.window
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.Image
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType

actual class PlatformFileHandler(
    val rootPath: String = window.location.protocol + "//" + window.location.host + window.location.pathname,
    val audioContext: AudioContext,
    actual val logger: Logger,
    actual val gameContext: GameContext
) {

    @ExperimentalStdlibApi
    actual fun read(filename: String): Content<String> {
        return asyncContent(filename) { it.toByteArray().decodeToString() }
    }

    @ExperimentalStdlibApi
    actual fun readData(filename: String): Content<ByteArray> {
        return asyncContent(filename) { it.toByteArray() }
    }

    actual fun readTextureImage(filename: String): Content<TextureImage> {
        val img = Image()
        img.src = computeUrl(filename)

        val content = Content<TextureImage>(filename, logger)

        img.addEventListener(
            "load",
            object : EventListener {
                override fun handleEvent(event: Event) {
                    content.load(
                        TextureImage(
                            source = img,
                            width = img.width,
                            height = img.height
                        )
                    )
                }
            }
        )
        return content
    }

    actual fun readSound(filename: String): Content<Sound> {
        return asyncContent(filename) { it }.flatMap { bytes ->
            val content = Content<Sound>(filename, logger)
            audioContext.decodeAudioData(bytes) { buffer ->
                content.load(Sound(buffer, audioContext))
            }
            content
        }
    }

    // https://youtrack.jetbrains.com/issue/KT-30098
    fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()

    private fun <T> asyncContent(filename: String, enc: (ArrayBuffer) -> T): Content<T> {
        val url = computeUrl(filename)

        val jsonFile = XMLHttpRequest()
        jsonFile.responseType = XMLHttpRequestResponseType.Companion.ARRAYBUFFER
        jsonFile.open("GET", url, true)

        val content = Content<T>(filename, logger)

        jsonFile.onload = { _ ->
            if (jsonFile.readyState == 4.toShort() && jsonFile.status == 200.toShort()) {
                val element = enc(jsonFile.response as ArrayBuffer)
                content.load(element)
            }
        }

        jsonFile.send()
        return content
    }

    private fun computeUrl(filename: String): String {
        val path = rootPath
        val url = path + filename
        return url
    }
}
