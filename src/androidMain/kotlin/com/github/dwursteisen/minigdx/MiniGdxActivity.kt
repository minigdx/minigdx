package com.github.dwursteisen.minigdx

import android.app.Activity
import android.os.Bundle

abstract class MiniGdxActivity(private val gameName: String = "missing game name", private val debug: Boolean = false) : Activity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        GameApplicationBuilder(
            gameConfigurationFactory = {
                GameConfiguration(gameName, debug, this)
            },
            gameFactory = {
                createGame(it)
            }
        )
        super.onCreate(savedInstanceState)
    }

    @ExperimentalStdlibApi
    abstract fun createGame(gameContext: GameContext): Screen
}
