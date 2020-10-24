package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.ecs.states.State

abstract class StateMachineComponent(var state: State? = null) :
    Component
