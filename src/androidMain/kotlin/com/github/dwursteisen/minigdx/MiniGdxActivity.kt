package com.github.dwursteisen.minigdx

import android.app.Activity
import android.os.Bundle

abstract class MiniGdxActivity : Activity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        configuration(GLConfiguration(this)).run { createGame() }
        super.onCreate(savedInstanceState)
    }

    @ExperimentalStdlibApi
    abstract fun createGame(): Game
}
