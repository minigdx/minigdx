package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Pixel
import com.github.dwursteisen.minigdx.Seconds
import kotlin.math.cos

class WaveEffect(
    val parent: TextEffect,
    // time per letter
    val frequency: Seconds = 0.05f,
    // amplitude of the letter for the effect.
    // The unit is pixel unit regarding the letter size.
    val amplitude: Pixel = 10
) : TextEffect {

    private var time = 0f

    override var isFinished: Boolean = false

    override var wasUpdated: Boolean = true

    override var content: String
        get() = parent.content
        set(value) {
            time = 0f
            parent.content = value
        }

    override fun update(delta: Seconds) {
        time += delta
        parent.update(delta)
    }

    override fun getAlteration(characterIndex: Int): Alteration {
        val progress = time / frequency
        val yOffset = (cos(characterIndex / 4f + progress)) * amplitude
        val alteration = Alteration(0, yOffset.toInt(), 0, 0, 0)
        return alteration.apply(parent.getAlteration(characterIndex))
    }
}
