package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Screen

@ExperimentalStdlibApi
class BirdScreen(override val gameContext: GameContext) : Screen {

    private val bird: Scene by gameContext.fileHandler.get("v2/bird.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        val models = bird.children.filter { it.type == ObjectType.ARMATURE }
        models.forEach { model ->
            gameContext.logger.info("DEMO") { "Create animated model '${model.name}'" }
            entityFactory.createFromNode(model, bird)
        }

        val cameras = bird.children.filter { it.type == ObjectType.CAMERA }
        cameras.forEach { camera ->
            gameContext.logger.info("DEMO") { "Create Camera model '${camera.name}'" }
            entityFactory.createFromNode(camera, bird)
        }
    }
}
