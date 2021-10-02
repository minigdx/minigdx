package com.github.dwursteisen.minigdx.game

interface StoryboardEvent

sealed class StoryboardAction {

    object StayHere : StoryboardAction()

    class Back internal constructor(val destroyMe: Boolean) : StoryboardAction()

    class SwitchTo internal constructor(
        val name: String?,
        val factoryMethod: () -> Game
    ) : StoryboardAction()

    class ReplaceWith internal constructor(
        val factoryMethod: () -> Game
    ) : StoryboardAction()
}

object Storyboard {

    /**
     * Change the current game to another game.
     *
     * The current game will be paused.
     * The new game will be used instead.
     *
     * To go back to the current game, the new game can use [back].
     * If the new game was already created, the previous instance will be use instead.
     */
    fun switchTo(
        /**
         * Name of the screen.
         */
        name: String?,
        /**
         * Instruction to create the new game.
         */
        factoryMethod: () -> Game
    ) = StoryboardAction.SwitchTo(
        name,
        factoryMethod
    )

    /**
     * Replace the current game with a new game.
     * The current game will be destroyed and the new game will be created.
     */
    fun replaceWith(
        /**
         * Instruction to create the new game.
         */
        factoryMethod: () -> Game
    ): StoryboardAction = StoryboardAction.ReplaceWith(factoryMethod)

    /**
     * Back to the previous game that called [switchTo].
     *
     * An error will be thrown if there was no previous game.
     */
    fun back(destroyMe: Boolean = false) = StoryboardAction.Back(destroyMe)

    /**
     * Do nothing.
     */
    fun stayHere() = StoryboardAction.StayHere
}
