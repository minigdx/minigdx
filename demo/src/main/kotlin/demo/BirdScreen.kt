package demo

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.createFrom
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.AnimatedModel

@ExperimentalStdlibApi
class BirdScreen(override val gameContext: GameContext) : Screen {

    private val bird: Scene by gameContext.fileHandler.get("v2/bird.protobuf")

    override fun createEntities(engine: Engine) {
        bird.models.values.forEach { model ->
            log.info("DEMO") { "Create animated model '${model.name}'" }
            engine.createFrom(model, bird, gameContext)
        }

        bird.perspectiveCameras.values.forEach { camera ->
            log.info("DEMO") { "Create Camera model '${camera.name}'" }
            engine.createFrom(camera, gameContext)
        }
    }
}
