package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.test.Test
import kotlin.test.assertEquals

class StateMachineSystemTest {

    class OnOffStateComponent : StateMachineComponent()

    class OnOffStateMachine : StateMachineSystem(OnOffStateComponent::class) {

        override fun initialState(entity: Entity): State {
            return OnState()
        }
    }

    class OnEvent : Event
    class OffEvent : Event

    class OnState : State() {

        override fun configure() {
            onEvent(OffEvent::class) {
                OffState()
            }
        }

        override fun update(delta: Seconds, entity: Entity): State? = null
    }

    class OffState : State() {
        override fun configure() {
            onEvent(OnEvent::class) {
                OnState()
            }
        }

        override fun update(delta: Seconds, entity: Entity): State? = null
    }

    private val engine = Engine()

    @Test
    fun state_machine_it_describes_a_state_machine() {
        val system = OnOffStateMachine()

        engine.addSystem(system)
        val entity = engine.create {
            add(OnOffStateComponent())
        }

        system.update(0f) // state initialization.

        system.onEvent(OffEvent(), entity)
        // Move to OffState
        with(entity.get(OnOffStateComponent::class).state!!) {
            assertEquals(OffState::class, this::class)
        }
        system.onEvent(OffEvent(), entity)
        // didn't change
        with(entity.get(OnOffStateComponent::class).state!!) {
            assertEquals(OffState::class, this::class)
        }
        system.onEvent(OnEvent(), entity)
        // Move back to OnState
        with(entity.get(OnOffStateComponent::class).state!!) {
            assertEquals(OnState::class, this::class)
        }
    }
}
