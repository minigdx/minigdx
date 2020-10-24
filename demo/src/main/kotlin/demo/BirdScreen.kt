package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.createModel
import com.github.dwursteisen.minigdx.game.Screen

@ExperimentalStdlibApi
class BirdScreen(override val gameContext: GameContext) : Screen {

    private val bird: Scene by gameContext.fileHandler.get("v2/bird.protobuf")

    override fun createEntities(engine: Engine) {
        val models = bird.children.filter { it.type == ObjectType.ARMATURE }
        models.forEach { model ->
            gameContext.logger.info("DEMO") { "Create animated model '${model.name}'" }
            engine.createFromNode(model, gameContext, bird)
        }

        val cameras = bird.children.filter { it.type == ObjectType.CAMERA }
        cameras.forEach { camera ->
            gameContext.logger.info("DEMO") { "Create Camera model '${camera.name}'" }
            engine.createFromNode(camera, gameContext, bird)
        }
    }
}
