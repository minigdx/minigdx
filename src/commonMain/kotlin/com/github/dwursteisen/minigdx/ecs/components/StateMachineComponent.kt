package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.ecs.states.State
import kotlin.reflect.KClass

abstract class StateMachineComponent(var state: State? = null) : Component {

    fun hasCurrentState(classState: KClass<out State>): Boolean {
        return classState.isInstance(state)
    }
}
