package com.github.dwursteisen.minigdx.ecs.entities

import com.github.dwursteisen.minigdx.ecs.components.Position

val Entity.position: Position
    get() = this.get(Position::class)
