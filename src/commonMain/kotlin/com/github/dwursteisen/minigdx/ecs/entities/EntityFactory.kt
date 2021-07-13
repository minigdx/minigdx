package com.github.dwursteisen.minigdx.ecs.entities

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.text.TextEffect
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.graph.GraphNode
import com.github.dwursteisen.minigdx.graph.Sprite

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
     * Create an entity from a node
     */
    fun createFromNode(node: GraphNode, parent: Entity? = null): Entity

    /**
     * Create a (hit)box of the scale of the [node] object at the position of [transformation].
     */
    fun createBox(node: GraphNode): Entity

    /**
     * Create a text using the [text], with the characters of the [font] at the
     * position of [transformation].
     */
    fun createText(text: String, font: Font, node: GraphNode): Entity

    /**
     * Create a text using the [textEffect] and the [font].
     *
     * @see createText
     */
    fun createText(text: TextEffect, font: Font, node: GraphNode): Entity

    /**
     * Create a 3D model using the [node] from the [scene] and applying the
     * [transformation].
     */
    fun createModel(node: GraphNode): Entity

    /**
     * Create an entity from an armature and the attached model.
     */
    fun createAnimatedModel(node: GraphNode): Entity

    /**
     * Create a light
     */
    fun createLight(node: GraphNode): Entity

    /**
     * Create an entity with Camera characteristics
     */
    fun createCamera(node: GraphNode): Entity

    /**
     * Create a text using the [textEffect] and the [font] but without any position information.
     */
    fun createText(textEffect: TextEffect, font: Font): Entity

    /**
     * Create an entity using a [Sprite]
     */
    fun createSprite(sprite: Sprite, position: Mat4 = Mat4.identity()): Entity
}
