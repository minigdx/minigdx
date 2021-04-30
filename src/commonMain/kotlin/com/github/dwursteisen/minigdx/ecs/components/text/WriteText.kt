package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Seconds

class WriteText(content: String) : TextEffect {

    override var content: String = content
        set(value) {
            field = value
            wasUpdated = true
        }

    override var isFinished: Boolean = true

    override var wasUpdated: Boolean = true

    override fun update(delta: Seconds) {
        wasUpdated = false
    }

    override fun getAlteration(characterIndex: Int): Alteration = Alteration.none
}
