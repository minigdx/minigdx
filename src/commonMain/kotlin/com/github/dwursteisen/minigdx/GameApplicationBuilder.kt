package com.github.dwursteisen.minigdx

class GameApplicationBuilder(
    val gameConfigurationFactory: () -> GameConfiguration,
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
        val gameContext = GameContext(platformContext)
        platformContext.start(gameContext, gameFactory)
    }
}
