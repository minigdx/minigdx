package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.graph.GraphScene
import com.github.dwursteisen.minigdx.graph.GraphSceneOptions

/**
 * Load a Graph Scene from a protobuf file.
 */
class GraphSceneLoader : FileLoader<GraphScene> {

    private val sceneLoader = SceneLoader()

    override fun load(filename: String, handler: FileHandler): Content<GraphScene> {
        return sceneLoader.load(filename, handler).map { scene ->
            GraphScene(
                filename = filename,
                scene = scene,
                assetsManager = handler.gameContext.assetsManager,
                fileHandler = handler.gameContext.fileHandler,
                options = GraphSceneOptions(
                    jointLimit = handler.gameContext.options.jointLimit
                )
            )
        }
    }
}
