package com.github.dwursteisen.minigdx

import android.app.Activity
import android.os.Bundle

abstract class MiniGdxActivity : Activity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        configuration(GLConfiguration(this)).execute { createGame(it) }
        super.onCreate(savedInstanceState)
    }

    @ExperimentalStdlibApi
    abstract fun createGame(gameContext: GameContext): Game
}
