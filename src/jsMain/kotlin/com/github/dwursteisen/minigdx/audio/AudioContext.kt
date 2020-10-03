package com.github.dwursteisen.minigdx.audio

import org.khronos.webgl.ArrayBuffer

external class JsSound

external class JsSoundDestination

external class JsBufferSource {
    var buffer: JsSound

    fun connect(destination: JsSoundDestination)

    fun start(delay: Int)
}

external class AudioContext {

    val destination: JsSoundDestination

    fun decodeAudioData(bytes: ArrayBuffer, onLoad: (buffer: JsSound) -> Unit)

    fun createBufferSource(): JsBufferSource
}
