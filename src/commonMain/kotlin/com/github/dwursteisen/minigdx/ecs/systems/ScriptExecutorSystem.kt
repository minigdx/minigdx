package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.ScriptComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.script.ScriptContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * Execute script in a coroutine context (one context per script)
 */
class ScriptExecutorSystem : System(EntityQuery(ScriptComponent::class)) {

    private class MutexScriptContext : ScriptContext {

        val mutex: Mutex = Mutex()

        override var delta: Seconds = 0f
            internal set

        override suspend fun yield() {
            mutex.lock()
        }
    }

    private var context = emptyList<MutexScriptContext>()

    override fun onEntityAdded(entity: Entity) {
        val scriptContext = MutexScriptContext()
        context = context + scriptContext
        GlobalScope.launch {
            entity.get(ScriptComponent::class).execute(scriptContext)
            context = context - scriptContext
        }
    }

    override fun update(delta: Seconds) {
        context.forEach {
            it.delta = delta
            if (it.mutex.isLocked) {
                it.mutex.unlock()
            }
        }
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
