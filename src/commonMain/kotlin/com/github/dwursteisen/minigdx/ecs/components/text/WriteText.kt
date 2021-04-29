package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Seconds

class WriteText(override var content: String) : TextEffect {

    override var wasUpdated: Boolean = false

    override val parent: TextEffect? = null

    override fun update(delta: Seconds) = Unit

    override fun getAlteration(characterIndex: Int, delta: Seconds): Alteration = Alteration.none
}
