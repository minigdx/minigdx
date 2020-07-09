package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler

class GameContext(
    private val glContext: GLContext
) {
    val gl: GL = glContext.createContext()
    val fileHandler: FileHandler = glContext.createFileHandler()
    val input: InputHandler = glContext.createInputHandler()
    val viewport: ViewportStrategy = glContext.createViewportStrategy()

    val ratio = gl.screen.ratio

    fun execute(block: (GameContext) -> Game) {
        glContext.run(this, block)
    }
}
