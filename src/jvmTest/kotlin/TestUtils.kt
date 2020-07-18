import com.github.dwursteisen.minigdx.logger.JavaLoggingLogger
import com.github.dwursteisen.minigdx.logger.Logger

actual fun createLogger(): Logger {
    return JavaLoggingLogger("test")
}
