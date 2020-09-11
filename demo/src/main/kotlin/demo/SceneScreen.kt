package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.math.AddTranslation
import com.github.dwursteisen.minigdx.math.Local

class Player : Component

class PlayerSystem(val inputHandler: InputHandler) : System(EntityQuery(Player::class)) {
    override fun update(delta: Seconds, entity: Entity) {
        if (inputHandler.isKeyPressed(Key.ARROW_RIGHT)) {
            entity.get(Position::class).apply(AddTranslation(5 * delta, origin = Local))
        } else if (inputHandler.isKeyPressed(Key.ARROW_LEFT)) {
            entity.get(Position::class).apply(AddTranslation(-5 * delta, origin = Local))
        }

        if (inputHandler.isKeyPressed(Key.ARROW_DOWN)) {
            entity.get(Position::class).apply(AddTranslation(z = 5 * delta, origin = Local))
        } else if (inputHandler.isKeyPressed(Key.ARROW_UP)) {
            entity.get(Position::class).apply(AddTranslation(z = -5 * delta, origin = Local))
        }
    }
}

@ExperimentalStdlibApi
class SceneScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("proto/paper.protobuf")

    override fun createEntities(engine: Engine) {
        val models = scene.children.filter { it.type == ObjectType.MODEL }
        models.forEach { node ->
            val entity = engine.createFromNode(node, gameContext, scene)
            if (node.name.toLowerCase() == "player") {
                entity.add(Player())
            }
        }

        val cameras = scene.children.filter { it.type == ObjectType.CAMERA }
        cameras.forEach { node ->
            engine.createFromNode(node, gameContext, scene)
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(PlayerSystem(gameContext.input)) + super.createSystems(engine)
    }
}


@ExperimentalStdlibApi
class SceneGame(gameContext: GameContext) : GameSystem(gameContext, SceneScreen(gameContext))

