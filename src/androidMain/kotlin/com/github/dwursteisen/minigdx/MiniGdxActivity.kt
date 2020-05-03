package com.github.dwursteisen.minigdx

import android.app.Activity
import android.os.Bundle
import com.github.dwursteisen.minigdx.demo.DemoGame

class MiniGdxActivity : Activity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        configuration(GLConfiguration(this)).run { createGame() }
        super.onCreate(savedInstanceState)
    }

    // TODO: should be abstract
    @ExperimentalStdlibApi
    fun createGame(): Game {
        return DemoGame()
    }
}
