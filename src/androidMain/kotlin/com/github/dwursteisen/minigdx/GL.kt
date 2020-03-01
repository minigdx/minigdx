package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.input.InputHandler

actual class GLConfiguration

actual class GLContext actual constructor(configuration: GLConfiguration) {
    internal actual fun createContext(): GL {
        // FIXME: get actual canvas size.
        return AndroidGL(Canvas(400, 400))
    }

    internal actual fun createFileHandler(): FileHandler {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    internal actual fun createInputHandler(): InputHandler {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    actual fun run(gameFactory: () -> Game) {
    }
}
