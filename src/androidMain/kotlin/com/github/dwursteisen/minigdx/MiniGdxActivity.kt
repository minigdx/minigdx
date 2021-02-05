package com.github.dwursteisen.minigdx

import android.app.Activity
import android.os.Bundle
import com.github.dwursteisen.minigdx.game.Game

abstract class MiniGdxActivity(
    private val gameName: String = "missing game name",
    private val gameScreenConfiguration: GameScreenConfiguration,
    private val debug: Boolean = false
) : Activity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        GameApplicationBuilder(
                gameConfigurationFactory = {
                    GameConfiguration(gameName,
                            gameScreenConfiguration,
                            debug,
                            this)
                },
                gameFactory = {
                    createGame(it)
                }
        ).start()
        super.onCreate(savedInstanceState)
    }

    @ExperimentalStdlibApi
    abstract fun createGame(gameContext: GameContext): Game
}
