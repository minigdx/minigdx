package com.github.dwursteisen.minigdx.file

import java.nio.ShortBuffer
import org.lwjgl.openal.AL10.AL_FORMAT_MONO16
import org.lwjgl.openal.AL10.AL_FORMAT_STEREO16
import org.lwjgl.openal.AL10.alBufferData
import org.lwjgl.openal.AL10.alGenBuffers

actual class Sound(private val buffer: ShortBuffer, val channels: Int, val sampleRate: Int) {

    init {
        val bufferID = alGenBuffers()
        alBufferData(
            bufferID,
            if (channels > 1) AL_FORMAT_STEREO16 else AL_FORMAT_MONO16,
            buffer,
            sampleRate
        )
    }

    actual fun play(loop: Int) {
        // TODO : https://ffainelli.github.io/openal-example/
        /*
        var sourceID: Int = audio.obtainSource(false)
        if (sourceID == -1) {
            // Attempt to recover by stopping the least recently played sound
            audio.retain(this, true)
            sourceID = audio.obtainSource(false)
        } else audio.retain(this, false)
        // In case it still didn't work
        // In case it still didn't work
        if (sourceID == -1) return -1
        val soundId: Long = audio.getSoundId(sourceID)
        alSourcei(sourceID, AL_BUFFER, bufferID)
        alSourcei(sourceID, AL_LOOPING, AL_FALSE)
        alSourcef(sourceID, AL_GAIN, volume)
        alSourcePlay(sourceID)
        return soundId*/

        TODO()
    }
}
