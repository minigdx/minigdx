package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine

class GameWrapper(val gameContext: GameContext, game: Game) {

    private var currentGame = GameNode(
        "root",
        game,
        Engine(gameContext)
    )

    fun create() {
        currentGame.bootstrap(gameContext)
    }

    fun resume() = Unit

    fun render(delta: Seconds) {
        gameContext.storyboardEvent?.let(::executeStoryboardEvent)
        currentGame.game.render(currentGame.engine, delta)
    }

    fun pause() = Unit

    fun destroy() {
        currentGame.game.destroy(currentGame.engine)
    }

    private fun executeStoryboardEvent(event: StoryboardEvent) {
        val action = currentGame.game.createStoryBoard(event)
        when (action) {
            is StoryboardAction.Back -> back(action)
            is StoryboardAction.SwitchTo -> switchTo(action)
            is StoryboardAction.ReplaceWith -> replaceWith(action)
            StoryboardAction.StayHere -> Unit
        }
    }

    private fun replaceWith(action: StoryboardAction.ReplaceWith) {
        val previousGame = currentGame
        currentGame = GameNode(
            "undefined",
            action.factoryMethod(),
            Engine(gameContext)
        )
        currentGame.bootstrap(gameContext)
        previousGame.game.destroy(previousGame.engine)
    }

    private fun switchTo(action: StoryboardAction.SwitchTo) {
        if (action.name == null) {
            currentGame = GameNode(
                "undefined",
                action.factoryMethod(),
                Engine(gameContext),
                currentGame
            )
            currentGame.bootstrap(gameContext)
        } else {
            val child = currentGame.children.firstOrNull { node -> node.name == action.name }
            if (child == null) {
                val gameNode = GameNode(
                    action.name,
                    action.factoryMethod(),
                    Engine(gameContext),
                    currentGame
                )
                currentGame.children = currentGame.children + gameNode
                currentGame = gameNode
                currentGame.bootstrap(gameContext)
            } else {
                currentGame = child
            }
        }
    }

    private fun back(action: StoryboardAction.Back) {
        val parent = currentGame.parent ?: throw IllegalStateException(
            "Trying to going back to the previous game." +
                "But there is not previous game available! " +
                "The back event is called too early?"
        )
        val previousGame = currentGame.game
        val previousEngine = currentGame.engine
        currentGame = parent
        if (action.destroyMe) {
            currentGame.children = currentGame.children.filter { node ->
                node.game != previousGame
            }
            previousGame.destroy(previousEngine)
        }
    }
}
