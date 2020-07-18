package com.github.dwursteisen.minigdx.logger

class JsLogger(private val gameName: String) : Logger {

    override fun debug(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.DEBUG.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
        }
    }

    override fun debug(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.DEBUG.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
            console.log(exception)
        }
    }

    override fun info(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.INFO.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
        }
    }

    override fun info(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.INFO.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
            console.log(exception)
        }
    }

    override fun warn(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.WARN.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
        }
    }

    override fun warn(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.WARN.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
            console.log(exception)
        }
    }

    override fun error(tag: String, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.ERROR.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
        }
    }

    override fun error(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal >= Logger.LogLevel.ERROR.ordinal) {
            console.log("($gameName)[$tag] ${message.invoke()}")
            console.log(exception)
        }
    }

    override var rootLevel: Logger.LogLevel = Logger.LogLevel.INFO
}
