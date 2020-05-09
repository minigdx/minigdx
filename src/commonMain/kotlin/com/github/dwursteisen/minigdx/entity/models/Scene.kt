package com.github.dwursteisen.minigdx.entity.models

import com.github.dwursteisen.minigdx.entity.delegate.Model

class Scene(
    val models: Map<String, Model> = emptyMap(),
    val animatedModels: Map<String, Model> = emptyMap(),
    val camera: Map<String, Camera3D> = emptyMap()
)
