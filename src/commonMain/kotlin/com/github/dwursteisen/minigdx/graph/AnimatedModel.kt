package com.github.dwursteisen.minigdx.graph

import com.dwursteisen.minigdx.scene.api.armature.Animation
import com.dwursteisen.minigdx.scene.api.armature.Armature
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.file.Asset

class AnimatedModel(
    val models: List<Model>,
    val animations: Map<String, Animation>,
    val referencePose: Armature
) : Asset {

    override fun load(gameContext: GameContext) = Unit
}
