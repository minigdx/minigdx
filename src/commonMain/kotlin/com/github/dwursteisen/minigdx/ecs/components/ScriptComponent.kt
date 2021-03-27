package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.ecs.script.ScriptContext

/**
 * Script component to pass a script.
 *
 * A script is a set of suspendable functions.
 *
 */
class ScriptComponent(var script: suspend ScriptContext.() -> Unit) : Component {

    suspend fun execute(context: ScriptContext) {
        script.invoke(context)
    }
}
