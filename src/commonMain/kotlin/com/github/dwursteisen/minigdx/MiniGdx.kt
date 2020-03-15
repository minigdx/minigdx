package com.github.dwursteisen.minigdx

fun configuration(configuration: GLConfiguration): GLContext {
    val glContext = GLContext(configuration)
    gl = glContext.createContext()
    fileHandler = glContext.createFileHandler()
    inputs = glContext.createInputHandler()
    viewport = glContext.createViewportStrategy()
    log = glContext.createLogger()
    return glContext
}
