package com.github.dwursteisen.minigdx.logger

interface Logger {

    enum class LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    fun debug(tag: String, message: () -> String)
    fun debug(tag: String, exception: Throwable, message: () -> String)

    fun info(tag: String, message: () -> String)
    fun info(tag: String, exception: Throwable, message: () -> String)

    fun warn(tag: String, message: () -> String)
    fun warn(tag: String, exception: Throwable, message: () -> String)

    fun error(tag: String, message: () -> String)
    fun error(tag: String, exception: Throwable, message: () -> String)

    var rootLevel: LogLevel
}
