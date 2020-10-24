package com.github.dwursteisen.minigdx.ecs.components.gl

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.ecs.components.Component

interface GLResourceComponent : Component {
    var id: Id
    var isDirty: Boolean
}
