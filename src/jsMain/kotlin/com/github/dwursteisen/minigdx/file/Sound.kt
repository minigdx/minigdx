package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.audio.AudioContext
import com.github.dwursteisen.minigdx.audio.JsBufferSource
import com.github.dwursteisen.minigdx.audio.JsSound

actual class Sound(private val sound: JsSound, private val audioContext: AudioContext) {

    actual fun play(loop: Int) {
        val source: JsBufferSource = audioContext.createBufferSource()
        source.buffer = sound
        source.connect(audioContext.destination)
        source.start(0)
    }
}
