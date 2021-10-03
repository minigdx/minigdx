package com.github.dwursteisen.minigdx.file

import android.media.SoundPool

actual class Sound(
    private val soundPool: SoundPool,
    private val soundId: Int
) {
    actual fun play(loop: Int) {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }
}
