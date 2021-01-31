import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Options
import com.github.dwursteisen.minigdx.PlatformContext
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.MockGL
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

class MockPlatformContext : PlatformContext {
    override fun createGL(): GL = MockGL()

    override fun createFileHandler(logger: Logger): FileHandler = MockFileFandler()

    override fun createInputHandler(logger: Logger): InputHandler = MockInputHandler()

    override fun createViewportStrategy(logger: Logger): ViewportStrategy = FillViewportStrategy(logger)

    override fun createLogger(): Logger = MockLogger()

    override fun createOptions(): Options = Options(true)

    override fun start(gameContext: GameContext, gameFactory: (GameContext) -> Game) = Unit
}
