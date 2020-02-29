package threed

import threed.file.FileHandler
import threed.input.InputHandler

actual class GLConfiguration

actual class GLContext actual constructor(configuration: GLConfiguration) {
    internal actual fun createContext(): GL {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
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
