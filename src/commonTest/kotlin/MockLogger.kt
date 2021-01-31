import com.github.dwursteisen.minigdx.logger.Logger

class MockLogger() : Logger {
    override fun debug(tag: String, message: () -> String) = Unit

    override fun debug(tag: String, exception: Throwable, message: () -> String) = Unit

    override fun info(tag: String, message: () -> String) = Unit

    override fun info(tag: String, exception: Throwable, message: () -> String) = Unit

    override fun warn(tag: String, message: () -> String) = Unit

    override fun warn(tag: String, exception: Throwable, message: () -> String) = Unit

    override fun error(tag: String, message: () -> String) = Unit

    override fun error(tag: String, exception: Throwable, message: () -> String) = Unit

    override var rootLevel: Logger.LogLevel = Logger.LogLevel.DEBUG
}
