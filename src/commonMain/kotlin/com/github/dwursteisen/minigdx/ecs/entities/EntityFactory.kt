package com.github.dwursteisen.minigdx.ecs.entities

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.text.TextEffect
import com.github.dwursteisen.minigdx.file.Font

interface EntityFactory {
    var engine: Engine
    var gameContext: GameContext

    /**
     * Create an entity from scratch.
     *
     * All components need to be added manually.
     */
    fun create(block: Engine.EntityBuilder.() -> Unit): Entity

    /**
     * Create an entity using the information from the [node] of the [scene].
     *
     * The node can be put at a different place using the [transformation].
     */
    fun createFromNode(node: Node, scene: Scene, parent: Entity? = null): Entity

    /**
     * Create a (hit)box of the scale of the [node] object at the position of [transformation].
     */
    fun createBox(node: Node): Entity

    /**
     * Create a text using the [text], with the characters of the [font] at the
     * position of [transformation].
     */
    fun createText(text: String, font: Font, node: Node): Entity
    fun createText(textEffect: TextEffect, font: Font, node: Node): Entity

    /**
     * Create a 3D model using the [node] from the [scene] and applying the
     * [transformation].
     */
    fun createModel(node: Node, scene: Scene): Entity

    /**
     * Create an entity from an armature and the attached model.
     */
    fun createArmature(node: Node, scene: Scene): Entity

    /**
     * Create an entity for the camera that can be use for the game interface.
     */
    fun createUICamera(): Entity

    fun createLight(node: Node, scene: Scene): Entity
}
