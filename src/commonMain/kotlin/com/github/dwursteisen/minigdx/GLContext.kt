package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

expect class GLContext(configuration: GLConfiguration) {
    internal fun createContext(): GL
    internal fun createFileHandler(): FileHandler
    internal fun createInputHandler(): InputHandler
    internal fun createViewportStrategy(): ViewportStrategy
    internal fun createLogger(): Logger

    fun run(gameFactory: (GL) -> Game)
}
