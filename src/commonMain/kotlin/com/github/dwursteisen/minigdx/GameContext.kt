package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.AssetsManager
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.FrameBuffer
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

class Options(var debug: Boolean)

class GameScreen(val width: Pixel, val height: Pixel, val ratio: Ratio = width / height.toFloat())

class DeviceScreen(var width: Pixel, var height: Pixel)

class GameContext(
    platformContext: PlatformContext,
    gameResolution: Resolution
) {
    val gl: GL = platformContext.createGL()
    val logger: Logger = platformContext.createLogger()

    val fileHandler: FileHandler = platformContext.createFileHandler(logger, this)
    val input: InputHandler = platformContext.createInputHandler(logger, this)
    val viewport: ViewportStrategy = platformContext.createViewportStrategy(logger)

    val assetsManager = AssetsManager(this)

    var frameBuffers: Map<String, FrameBuffer> = emptyMap()

    /**
     * Size of the game screen.
     * This screen has a fixed size and a fixed ratio.
     *
     * Elements out of this game screen will not be displayed
     * (for example if the device screen is resized)
     */
    val gameScreen: GameScreen = GameScreen(
        gameResolution.width,
        gameResolution.height,
        gameResolution.ratio
    )

    /**
     * Device screen resolution.
     *
     * This screen can be resized and so with and height can changed
     * through the time.
     *
     * This is the size of the window.
     */
    val deviceScreen: DeviceScreen = DeviceScreen(
        gameResolution.width,
        gameResolution.height
    )

    /**
     * Framebuffer resolution
     *
     * This framebuffer, liike the device screen, can changed
     * through the time
     *
     * This is the size of the framebuffer contained in the window.
     * The resolution can be different of the device screen, for example
     * on a Retina display screen in which the frame buffer screen
     * is different.
     */
    val frameBufferScreen: DeviceScreen = DeviceScreen(
        gameResolution.width,
        gameResolution.height
    )

    val options = platformContext.createOptions()

    fun logPlatform() {
        logger.info("MINIGDX") { "--- Platform information ---" }
        logger.info("MINIGDX") { "OpenGL Vendor    : \t" + gl.getString(GL.VENDOR) }
        logger.info("MINIGDX") { "OpenGL Renderer  : \t" + gl.getString(GL.RENDERER) }
        logger.info("MINIGDX") { "OpenGL Version   : \t" + gl.getString(GL.VERSION) }
        logger.info("MINIGDX") { "OpenGL Shading   : \t" + gl.getString(GL.SHADING_LANGUAGE_VERSION) }
        logger.info("MINIGDX") { "OpenGL Extensions: \t" + gl.getString(GL.EXTENSIONS) }
    }
}
