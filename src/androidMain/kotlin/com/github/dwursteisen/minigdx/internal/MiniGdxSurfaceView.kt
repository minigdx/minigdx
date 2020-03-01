package com.github.dwursteisen.minigdx.internal

import android.content.Context
import android.opengl.GLSurfaceView
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.MiniGdxActivity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MiniGdxSurfaceView(context: Context) : GLSurfaceView(context) {

    init {
        setEGLContextClientVersion(3)

        setRenderer(object : Renderer {

            private var time = 0f

            lateinit var game: Game

            override fun onDrawFrame(gl: GL10?) {
                val now = System.currentTimeMillis() / 1000f
                val delta = now - time
                game.render(delta)
                time = now
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) = Unit

            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                game = (context as MiniGdxActivity).createGame()
                game.create()
            }
        })
        // Render the view only when there is a change in the drawing data
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}
