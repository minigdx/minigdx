package com.github.dwursteisen.minigdx

import kotlin.browser.window
import org.w3c.dom.HTMLCanvasElement

actual class GameConfiguration(
    actual val gameName: String,
    actual val debug: Boolean,
    /**
     * Configuration of the game screen.
     *
     * Note that the game screen will be resized
     * regarding the current viewport strategy.
     */
    actual val gameScreenConfiguration: GameScreenConfiguration,
    val loadingListener: (Percent) -> Unit = { _ -> Unit },
    val canvas: HTMLCanvasElement? = null,
    val canvasId: String? = null,
    val rootPath: String = window.location.protocol + "//" + window.location.host + window.location.pathname
)
