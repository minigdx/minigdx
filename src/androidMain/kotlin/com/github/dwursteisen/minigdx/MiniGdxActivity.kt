package com.github.dwursteisen.minigdx

import android.app.Activity
import android.os.Bundle

class MiniGdxActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        configuration(GLConfiguration(this)).run { createGame() }
        super.onCreate(savedInstanceState)
    }

    // TODO: should be abstract
    fun createGame(): Game {
        return DemoGame()
    }
}
