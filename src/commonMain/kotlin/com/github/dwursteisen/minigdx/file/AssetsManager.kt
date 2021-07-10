package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.System

/**
 * Asset that can be updated through the time.
 */
interface Asset {

    /**
     * Load the asset in the system.
     * ie:
     * - load a model into Open GL
     * - load a sound into the Audio Context
     */
    fun load(gameContext: GameContext)
}

/**
 * Manage assets loading.
 */
class AssetsManager(private val gameContext: GameContext) {

    private val assets = mutableListOf<Asset>()

    fun add(asset: Asset) {
        assets.add(asset)
    }

    fun update() {
        assets.forEach { asset ->
            asset.load(gameContext)
        }
        assets.clear()
    }
}

class AssetsManagerSystem(private val assetsManager: AssetsManager) : System() {

    override fun update(delta: Seconds) {
        assetsManager.update()
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
