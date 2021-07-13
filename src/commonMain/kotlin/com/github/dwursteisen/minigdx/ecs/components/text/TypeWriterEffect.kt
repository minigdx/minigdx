package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Seconds
import kotlin.math.roundToInt

class TypeWriterEffect(
    val parent: TextEffect,
    // duration per letters
    var speed: Seconds = 0.05f,
    var loop: Boolean = false
) : TextEffect {

    var time: Seconds = 0f

    override var isFinished: Boolean = false

    override var wasUpdated: Boolean = false

    private var displayedContent = ""

    override var content: String
        get() = displayedContent
        set(value) {
            // setting a new content reset the effect
            isFinished = false
            time = 0f
            displayedContent = ""
            parent.content = value
        }

    override fun update(delta: Seconds) {
        parent.update(delta)
        if (parent.wasUpdated) {
            wasUpdated = true
        }
        // All the text is already displayed.
        if (isFinished) {
            if (loop) {
                isFinished = false
                time = 0f
                displayedContent = ""
            } else {
                return
            }
        }

        time += delta

        val parentContent = parent.content
        val numberOfLetter = (time / speed).roundToInt()
        val previousContentSize = content.length
        displayedContent = parentContent.take(numberOfLetter)

        wasUpdated = (previousContentSize != content.length)
        isFinished = parentContent.length == content.length
    }

    override fun getAlteration(characterIndex: Int): Alteration = Alteration.none
}
