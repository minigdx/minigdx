package com.github.dwursteisen.minigdx.ecs.systems

import ModelFactory.gameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.StateMachineComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.states.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

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

    private val engine = Engine(gameContext())

    @Test
    fun state_machine_it_describes_a_state_machine() {
        val system = OnOffStateMachine()

        engine.addSystem(system)
        val entity = engine.create {
            add(OnOffStateComponent())
        }

        engine.update(0f) // state initialization.
        engine.onGameStart()

        system.onEvent(OffEvent())
        // Move to OffState
        val offState = entity.get(OnOffStateComponent::class).state!!
        with(offState) {
            assertEquals(OffState::class, this::class)
        }
        system.onEvent(OffEvent())
        // didn't change
        with(entity.get(OnOffStateComponent::class).state!!) {
            assertEquals(OffState::class, this::class)
            assertSame(offState, this)
        }
        system.onEvent(OnEvent())
        // Move back to OnState
        with(entity.get(OnOffStateComponent::class).state!!) {
            assertEquals(OnState::class, this::class)
        }
    }
}
