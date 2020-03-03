package com.github.dwursteisen.minigdx

typealias Seconds = Float

data class WorldSize(val width: Int,val height: Int) {

    val ratio: Float = width / height.toFloat()
}

interface Game {

    val worldSize: WorldSize

    fun create() = Unit

    fun resume() = Unit

    fun render(delta: Seconds)

    fun pause() = Unit

    fun destroy() = Unit
}
