package com.github.dwursteisen.minigdx.internal

import android.content.Context
import android.opengl.GLSurfaceView
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.PlatformContext
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.game.GameWrapper
import com.github.dwursteisen.minigdx.input.InputManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.min

@ExperimentalStdlibApi
class MiniGdxSurfaceView(
    private val platformContext: PlatformContext,
    private val gameContext: GameContext,
    private val gameFactory: (gameContext: GameContext) -> Game,
    context: Context
) : GLSurfaceView(context) {

    init {
        setEGLContextClientVersion(2)

        setRenderer(
            object : Renderer {

                private var time = 0f

                lateinit var gameWrapper: GameWrapper
                lateinit var inputManager: InputManager

                override fun onDrawFrame(gl: GL10?) {
                    val now = System.nanoTime().toFloat()
                    val delta = (now - time) / 1000000000.0f
                    val deltaCapped = min(1 / 60f, delta)

                    // Get the last input
                    inputManager.record()
                    // Advance the game
                    gameWrapper.render(deltaCapped)
                    inputManager.reset()
                    platformContext.postRenderLoop()
                    platformContext.postRenderLoop = { }
                    time = now
                }

                override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                    gameContext.deviceScreen.height = height
                    gameContext.deviceScreen.width = width
                    gameContext.frameBufferScreen.height = height
                    gameContext.frameBufferScreen.width = width
                    gameContext.viewport.update(
                        gameContext.gl,
                        gameContext.frameBufferScreen.width,
                        gameContext.frameBufferScreen.height,
                        gameContext.gameScreen.width,
                        gameContext.gameScreen.height
                    )
                }

                override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                    gameContext.logPlatform()
                    inputManager = gameContext.input as InputManager
                    val game = gameFactory(gameContext)
                    gameWrapper = GameWrapper(gameContext, game)
                    gameWrapper.create()
                }
            }
        )
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_CONTINUOUSLY
        setOnTouchListener(gameContext.input as OnTouchListener)
    }
}
