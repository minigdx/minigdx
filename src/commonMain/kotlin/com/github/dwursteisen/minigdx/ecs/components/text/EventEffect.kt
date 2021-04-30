package com.github.dwursteisen.minigdx.ecs.components.text

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.events.EventQueue

class EventEffect(val event: Event, private val eventQueue: EventQueue) : TextEffect {

    override var isFinished: Boolean = false

    override var wasUpdated: Boolean = false

    override var content: String = ""

    override fun update(delta: Seconds) {
        // send event
        eventQueue.emit(event)
        isFinished = true
    }

    override fun getAlteration(characterIndex: Int): Alteration = Alteration.none
}
