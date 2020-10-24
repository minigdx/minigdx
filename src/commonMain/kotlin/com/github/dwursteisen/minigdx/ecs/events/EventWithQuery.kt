package com.github.dwursteisen.minigdx.ecs.events

import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery

internal class EventWithQuery(var event: Event, var entityQuery: EntityQuery? = null) {

    companion object {

        private class NoEvent : Event

        private class EventWithQueryObjectPool : ObjectPool<EventWithQuery>(10) {

            override fun newInstance(): EventWithQuery {
                return EventWithQuery(NO_EVENT, null)
            }

            override fun destroyInstance(obj: EventWithQuery) {
                obj.event = NO_EVENT
                obj.entityQuery = null
            }
        }

        private val NO_EVENT = NoEvent()

        private val objectPool = EventWithQueryObjectPool()

        fun of(event: Event, entityQuery: EntityQuery? = null): EventWithQuery {
            return objectPool.obtain().apply {
                this.event = event
                this.entityQuery = entityQuery
            }
        }
    }
}
