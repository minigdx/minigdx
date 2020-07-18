import com.github.dwursteisen.minigdx.logger.JsLogger
import com.github.dwursteisen.minigdx.logger.Logger

actual fun createLogger(): Logger {
    return JsLogger("test")
}
