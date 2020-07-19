package com.github.dwursteisen.minigdx.ecs.components.gl

import com.github.dwursteisen.minigdx.ecs.components.Component

interface GLResourceComponent : Component {
    var isDirty: Boolean
}
