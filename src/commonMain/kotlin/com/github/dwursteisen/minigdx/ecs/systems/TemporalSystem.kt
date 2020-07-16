package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds

abstract class TemporalSystem(private val frequency: Seconds, query: EntityQuery = EntityQuery()) : System(query) {

    private var timer = frequency

    override fun update(delta: Seconds) {
        timer -= delta

        if (timer < 0) {
            timer += frequency
            timeElapsed()
            super.update(delta)
        }
    }

    abstract fun timeElapsed()
}
