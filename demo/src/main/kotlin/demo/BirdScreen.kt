package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.createFrom
import com.github.dwursteisen.minigdx.game.Screen

@ExperimentalStdlibApi
class BirdScreen(override val gameContext: GameContext) : Screen {

    private val bird: Scene by gameContext.fileHandler.get("v2/bird.protobuf")

    override fun createEntities(engine: Engine) {
        bird.models.values.forEach { model ->
            gameContext.logger.info("DEMO") { "Create animated model '${model.name}'" }
            engine.createFrom(model, bird, gameContext)
        }

        bird.perspectiveCameras.values.forEach { camera ->
            gameContext.logger.info("DEMO") { "Create Camera model '${camera.name}'" }
            engine.createFrom(camera, gameContext)
        }
    }
}
