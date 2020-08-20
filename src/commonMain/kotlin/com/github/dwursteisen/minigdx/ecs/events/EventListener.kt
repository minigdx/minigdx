package com.github.dwursteisen.minigdx.ecs.events

import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery

interface EventListener {

    fun onEvent(event: Event, entityQuery: EntityQuery? = null)
}
