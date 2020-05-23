package com.github.dwursteisen.minigdx.entity.models

import com.github.dwursteisen.minigdx.entity.delegate.Drawable

class Scene(
    val models: Map<String, Drawable> = emptyMap(),
    val animatedModels: Map<String, Drawable> = emptyMap(),
    val camera: Map<String, Camera3D> = emptyMap()
)
