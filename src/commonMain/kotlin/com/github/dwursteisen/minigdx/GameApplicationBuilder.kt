package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.game.Game

class GameApplicationBuilder(
    /**
     * Factory to build the game configuration.
     */
    val gameConfigurationFactory: () -> GameConfiguration,

    /**
     * Factory to build the game.
     * The game should be instantiated through it and NOT earlier
     * otherwise resources required by the game might not yet exist.
     */
    val gameFactory: (gameContext: GameContext) -> Game
) {

    /**
     * Create the game configuration using [gameConfigurationFactory].
     * This configuration is used to create the game context.
     * The game is created using this game context using [gameFactory].
     */
    fun start() {
        val configuration = gameConfigurationFactory()
        val platformContext = PlatformContextCommon(configuration)
        platformContext.start(gameFactory)
    }
}
