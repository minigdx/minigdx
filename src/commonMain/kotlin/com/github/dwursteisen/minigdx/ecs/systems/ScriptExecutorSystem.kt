package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Script
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.script.ScriptContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * Execute script in a coroutine context (one context per script)
 */
class ScriptExecutorSystem : System(EntityQuery(Script::class)), ScriptContext {

    private val mutex = Mutex()
    override var delta: Seconds = 0f
        private set

    override suspend fun yield() {
        mutex.lock()
    }

    override fun onEntityAdded(entity: Entity) {
        GlobalScope.launch {
            entity.get(Script::class).execute(this@ScriptExecutorSystem)
        }
    }

    override fun update(delta: Seconds) {
        this.delta = delta
        if (mutex.isLocked) {
            mutex.unlock()
        }
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
