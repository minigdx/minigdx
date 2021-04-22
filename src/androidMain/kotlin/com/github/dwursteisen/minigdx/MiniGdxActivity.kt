package com.github.dwursteisen.minigdx

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.input.AndroidInputHandler

abstract class MiniGdxActivity(
    private val gameName: String = "missing game name",
    private val gameScreenConfiguration: GameScreenConfiguration,
    private val debug: Boolean = false
) : Activity() {

    private lateinit var inputHandler: AndroidInputHandler

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        GameApplicationBuilder(
            gameConfigurationFactory = {
                GameConfiguration(
                    gameName,
                    gameScreenConfiguration,
                    debug,
                    this
                )
            },
            gameFactory = { gameContext ->
                inputHandler = gameContext.input as AndroidInputHandler
                createGame(gameContext)
            }
        ).start()
        super.onCreate(savedInstanceState)
    }

    @ExperimentalStdlibApi
    abstract fun createGame(gameContext: GameContext): Game

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        inputHandler.onKeyDown(keyCode)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        inputHandler.onKeyUp(keyCode)
        return true
    }
}
