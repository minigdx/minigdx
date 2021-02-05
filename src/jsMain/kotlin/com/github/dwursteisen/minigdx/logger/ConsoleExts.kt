package com.github.dwursteisen.minigdx.logger

import kotlin.js.Console

external interface ChromeConsole : Console {

    fun time(name: String)

    fun timeEnd(name: String)
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline fun <T> profile(name: String, block: () -> T): T {
    val p = console as ChromeConsole
    // p.time(name)
    val result = block()
    // p.timeEnd(name)
    return result
}
