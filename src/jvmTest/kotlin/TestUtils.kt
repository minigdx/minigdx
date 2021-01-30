import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.GLContext
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.graphics.MockGL
import com.github.dwursteisen.minigdx.logger.JavaLoggingLogger
import com.github.dwursteisen.minigdx.logger.Logger

actual fun createLogger(): Logger {
    return JavaLoggingLogger("test")
}

actual fun createGlContext(): GLContext {
    return object : GLContext(
        GLConfiguration(
            name = "test-game",
            gameName = "test-game",
            width = 800,
            height = 600,
            debug = true
        )
    ) {
        override fun createContext(): GL = MockGL()

        override fun run(gameContext: GameContext, gameFactory: (GameContext) -> Game) = Unit
    }
}
