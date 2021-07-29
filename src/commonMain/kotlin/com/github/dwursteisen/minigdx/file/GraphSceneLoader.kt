package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.graph.GraphScene

/**
 * Load a Graph Scene from a protobuf file.
 */
class GraphSceneLoader : FileLoader<GraphScene> {

    private val sceneLoader = SceneLoader()

    override fun load(filename: String, handler: FileHandler): Content<GraphScene> {
        return sceneLoader.load(filename, handler).map { scene ->
            GraphScene(scene, handler.gameContext.assetsManager)
        }
    }
}
