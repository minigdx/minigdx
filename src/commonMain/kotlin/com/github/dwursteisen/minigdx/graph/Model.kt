package com.github.dwursteisen.minigdx.graph

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.file.Asset

/**
 * Float array with 3 floats per component
 */
typealias C3f = FloatArray
/**
 * Float array with 2 floats per component
 */
typealias C2f = FloatArray
/**
 * Float array with 4 floats per component
 */
typealias C4f = FloatArray

class Model(
    val primitives: List<Primitive>,
    var displayable: Boolean = false
) : Asset {

    override fun load(gameContext: GameContext) {
        primitives.forEach { it.load(gameContext) }
        displayable = true
    }
}
