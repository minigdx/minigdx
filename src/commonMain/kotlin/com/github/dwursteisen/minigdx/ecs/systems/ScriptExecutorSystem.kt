package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.ScriptComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.script.ScriptContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Execute script in a coroutine context (one context per script)
 */
class ScriptExecutorSystem : System(EntityQuery(ScriptComponent::class)) {

    private inner class MutexScriptContext : ScriptContext {

        val mutex: Mutex = Mutex()

        override var delta: Seconds = 0f
            internal set

        override suspend fun yield() {
            mutex.lock()
        }

        override suspend fun emit(event: Event, entityQuery: EntityQuery?) {
            lock.withLock {
                events.add(event to entityQuery)
            }
        }

        override suspend fun executeInGameLoop(block: () -> Unit) {
            lock.withLock {
                mainThread.add(block)
            }
        }
    }

    private var context = emptyList<MutexScriptContext>()

    private val lock = Mutex()

    private val toRemoveComponents = mutableListOf<Pair<ScriptComponent, Entity>>()
    private val events: MutableList<Pair<Event, EntityQuery?>> = mutableListOf()
    private val mainThread: MutableList<() -> Unit> = mutableListOf()

    private val scriptScope = MainScope()

    override fun onEntityAdded(entity: Entity) {
        entity.findAll(ScriptComponent::class).forEach { component ->
            val scriptContext = MutexScriptContext()
            context = context + scriptContext

            scriptScope.launch {
                component.execute(scriptContext)
                context = context - scriptContext
                lock.withLock {
                    toRemoveComponents.add(component to entity)
                }
            }
        }
    }

    override fun update(delta: Seconds) {
        context.forEach {
            it.delta = delta
            if (it.mutex.isLocked) {
                it.mutex.unlock()
            }
        }
        mainThread.execute {
            it.invoke()
        }
        events.execute { (event, query) ->
            emit(event, query)
        }
        toRemoveComponents.execute { (component, entity) ->
            entity.remove(component)
        }
    }

    override fun update(delta: Seconds, entity: Entity) = Unit

    private fun <T> MutableList<T>.execute(block: (T) -> Unit) {
        this.forEach(block)
        this.clear()
    }
}
