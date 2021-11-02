package com.github.dwursteisen.minigdx.ecs.components

import ModelFactory.gameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.states.State
import com.github.dwursteisen.minigdx.ecs.systems.StateMachineSystem
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StateMachineComponentTest {

    class OnState : State() {

        override fun update(delta: Seconds, entity: Entity): State? = null
    }

    class OffState : State() {

        override fun update(delta: Seconds, entity: Entity): State? = null
    }

    class OnOffStateComponent : StateMachineComponent()

    class OnOffStateMachine : StateMachineSystem(OnOffStateComponent::class) {

        override fun initialState(entity: Entity): State {
            return OnState()
        }
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

        assertTrue(entity.get(OnOffStateComponent::class).hasCurrentState(OnState::class))
        assertFalse(entity.get(OnOffStateComponent::class).hasCurrentState(OffState::class))
    }
}
