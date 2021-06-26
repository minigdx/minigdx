package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.graph.Model

class ModelComponent(
    var model: Model,
    /**
     * Is the the model is hidden?
     *
     * A hidden model will not be displayed
     */
    var hidden: Boolean = false
) : Component {

    /**
     * Is the model can be displayed?
     *
     * A Model can be displayed if every
     * technical requirement are set.
     *
     * ie: all open GL are set correctly.
     */
    val displayable: Boolean
        get() = model.displayble
}
