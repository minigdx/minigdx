package com.github.dwursteisen.minigdx

import kotlin.browser.window
import org.w3c.dom.HTMLCanvasElement

actual class GLConfiguration(
    actual val gameName: String,
    val canvas: HTMLCanvasElement? = null,
    val canvasId: String? = null,
    val rootPath: String = window.location.protocol + "//" + window.location.host + window.location.pathname
)
