
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
