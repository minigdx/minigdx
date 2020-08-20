package com.github.dwursteisen.minigdx.ecs.events

import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery

internal class EventWithQuery(var event: Event, var entityQuery: EntityQuery? = null) {

    companion object {

        // TODO: Can be create from an object Pool.
        fun of(event: Event, entityQuery: EntityQuery? = null): EventWithQuery {
            return EventWithQuery(event, entityQuery)
        }
    }
}
