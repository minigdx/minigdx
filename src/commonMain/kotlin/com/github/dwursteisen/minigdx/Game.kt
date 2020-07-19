package com.github.dwursteisen.minigdx

interface Game {

    fun create() = Unit

    fun resume() = Unit

    fun render(delta: Seconds)

    fun pause() = Unit

    fun destroy() = Unit
}
