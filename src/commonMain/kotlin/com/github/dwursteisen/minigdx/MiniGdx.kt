package com.github.dwursteisen.minigdx

@ExperimentalStdlibApi
fun configuration(configuration: GLConfiguration): GameContext {
    val glContext = GLContext(configuration)
    val gameContext = GameContext(glContext = glContext)
    log = glContext.createLogger()
    return gameContext
}
