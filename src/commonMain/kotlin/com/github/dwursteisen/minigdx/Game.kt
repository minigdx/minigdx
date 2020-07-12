package com.github.dwursteisen.minigdx

/**
 * Virtual resolution of your game.
 *
 * Create your 2D interface using this resolution.
 * The game will scale up your game to match this resolution.
 */
@Deprecated("see above")
data class WorldResolution(val width: Int, val height: Int) {

    val ratio: Float = width / height.toFloat()
}

interface Game {

    @Deprecated("world resolution in the new API has always the same values than gl.screen. Prefer gl.screen")
    val worldResolution: WorldResolution

    fun create() = Unit

    fun resume() = Unit

    fun render(delta: Seconds)

    fun pause() = Unit

    fun destroy() = Unit
}
