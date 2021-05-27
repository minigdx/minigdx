package com.github.dwursteisen.minigdx.utils

import com.github.dwursteisen.minigdx.DevicePosition
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.GamePosition

/**
 * Convert a device coordinate into a game screen coordinate
 */
fun GameContext.convert(x: DevicePosition, y: DevicePosition): Pair<GamePosition, GamePosition>? {
    val converted = this.viewport.convert(
        x,
        y,
        this.deviceScreen.width,
        this.deviceScreen.height,
        this.gameScreen.width,
        this.gameScreen.height
    )
    val (gameX, gameY) = converted
    return if (
        // x within the game screen
        (0 <= gameX && gameX <= this.gameScreen.width) &&
        // y within the game screen
        (0 <= gameY && gameY <= this.gameScreen.height)
    ) {
        converted
    } else {
        null
    }
}
