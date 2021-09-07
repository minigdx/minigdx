package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Seconds

class WriteText(content: String) : TextEffect {

    override var content: String = content
        set(value) {
            field = value
            _wasUpdated = true
        }

    override var isFinished: Boolean = true

    private var _wasUpdated: Boolean = true

    override var wasUpdated: Boolean = true

    override fun update(delta: Seconds) {
        wasUpdated = _wasUpdated
        _wasUpdated = false
    }

    override fun getAlteration(characterIndex: Int): Alteration = Alteration.none
}
