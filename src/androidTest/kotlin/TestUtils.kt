
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.GLContext
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.graphics.MockGL
import com.github.dwursteisen.minigdx.logger.Logger

actual fun createLogger(): Logger {
    return object : Logger {
        override fun debug(tag: String, message: () -> String) {
            System.out.println(message())
        }

        override fun debug(tag: String, exception: Throwable, message: () -> String) {
            System.out.println(message())
        }

        override fun info(tag: String, message: () -> String) {
            System.out.println(message())
        }

        override fun info(tag: String, exception: Throwable, message: () -> String) {
            System.out.println(message())
        }

        override fun warn(tag: String, message: () -> String) {
            System.out.println(message())
        }

        override fun warn(tag: String, exception: Throwable, message: () -> String) {
            System.out.println(message())
        }

        override fun error(tag: String, message: () -> String) {
            System.out.println(message())
        }

        override fun error(tag: String, exception: Throwable, message: () -> String) {
            System.out.println(message())
        }

        override var rootLevel: Logger.LogLevel = Logger.LogLevel.DEBUG
    }
}

actual fun createGlContext(): GLContext {
    return object : GLContext(
        GLConfiguration(
            "test-game", false, null
        )
    ) {
        override fun createContext(): GL = MockGL()

        override fun run(gameContext: GameContext, gameFactory: (GameContext) -> Game) = Unit

        override fun createFileHandler(logger: com.github.dwursteisen.minigdx.logger.Logger): com.github.dwursteisen.minigdx.file.FileHandler {
            TODO("Not Implemented")
        }
    }
}
