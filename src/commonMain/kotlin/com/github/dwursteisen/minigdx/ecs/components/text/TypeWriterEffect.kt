package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Seconds

class TypeWriterEffect(
    // duration per letters
    var speed: Seconds,
    override val parent: TextEffect?
) : TextEffect {

    var time: Seconds = 0f

    override var wasUpdated: Boolean = false

    override var content: String = ""

    override fun update(delta: Seconds) {
        parent ?: return

        parent.update(delta)
        val parentContent = parent.content
        // All the text is already displayed.
        if (parentContent.length * speed < time) {
            return
        }
        time += delta

        val numberOfLetter = (time / speed).toInt()
        val previousContentSize = parentContent.length
        content = parentContent.take(numberOfLetter)

        wasUpdated = (previousContentSize != content.length)
    }

    override fun getAlteration(characterIndex: Int, delta: Seconds): Alteration = Alteration.none
}
