package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Pixel
import com.github.dwursteisen.minigdx.Seconds

/**
 * Alteration of the character at the font level
 */
class Alteration(
    var x: Pixel,
    var y: Pixel,
    var z: Pixel,
    var with: Pixel,
    var height: Pixel
) {

    fun apply(alteration: Alteration): Alteration {
        x += alteration.x
        y += alteration.y
        z += alteration.z
        with += alteration.with
        height += alteration.height
        return this
    }

    companion object {

        val none = Alteration(0, 0, 0, 0, 0)
    }
}

interface TextEffect {

    var isFinished: Boolean

    var wasUpdated: Boolean

    var content: String

    fun update(delta: Seconds)

    fun getAlteration(characterIndex: Int): Alteration
}
