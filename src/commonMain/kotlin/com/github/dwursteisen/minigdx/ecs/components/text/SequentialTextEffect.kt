package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Seconds

class SequentialTextEffect(private val sequence: List<TextEffect>) : TextEffect {

    private var currentEffect = 0

    override var isFinished: Boolean
        get() = sequence.last().isFinished
        set(value) = Unit

    override var wasUpdated: Boolean
        get() = sequence[currentEffect].wasUpdated
        set(value) = Unit

    override var content: String
        get() {
            return sequence.map { it.content }.joinToString("")
        }
        set(value) = Unit

    override fun update(delta: Seconds) {
        sequence[currentEffect].update(delta)
        if (sequence[currentEffect].isFinished) {
            currentEffect = (currentEffect + 1) % sequence.size
        }
    }

    override fun getAlteration(characterIndex: Int): Alteration {
        return sequence[currentEffect].getAlteration(characterIndex)
    }
}
