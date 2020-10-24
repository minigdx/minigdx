package com.github.dwursteisen.minigdx

@ExperimentalStdlibApi
fun configuration(configuration: GLConfiguration): GameContext {
    val glContext = GLContext(configuration)
    val gameContext = GameContext(glContext = glContext)
    return gameContext
}

object MiniGdx {

    var debugHitbox: Boolean = false
    var debugStates: Boolean = true
}
