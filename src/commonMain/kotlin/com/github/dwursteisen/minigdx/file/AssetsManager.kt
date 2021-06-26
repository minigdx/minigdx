package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds

interface Asset {

    fun load(gameContext: GameContext)
}

class AssetsManager(private val gameContext: GameContext) {

    private val assets = mutableListOf<Asset>()

    fun add(asset: Asset) {
        assets.add(asset)
    }

    fun update(delta: Seconds) {
        assets.forEach { asset ->
            asset.load(gameContext)
        }
    }
}
