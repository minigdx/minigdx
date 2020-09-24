package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.createSprite
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import kotlin.math.cos

class Player : Component

class PlayerSystem(val inputHandler: InputHandler) : System(EntityQuery(Player::class)) {

    private var time = 0f

    override fun update(delta: Seconds, entity: Entity) {
        time += delta
        if (inputHandler.isKeyPressed(Key.ARROW_RIGHT)) {
            entity.position.addTranslate(x = 5, delta = delta)
        } else if (inputHandler.isKeyPressed(Key.ARROW_LEFT)) {
            entity.position.addTranslate(x = -5, delta = delta)
        }

        if (inputHandler.isKeyPressed(Key.ARROW_DOWN)) {
            entity.position.addTranslate(y = 5, delta = delta)
        } else if (inputHandler.isKeyPressed(Key.ARROW_UP)) {
            entity.position.addTranslate(y = -5, delta = delta)
        }

       // entity.position.setScale(x = cos(time) * 3f, y = cos(time) * 3f)
      //  entity.position.setRotationX(cos(time) * 360f)
    }
}

@ExperimentalStdlibApi
class SceneScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("proto/asteroid.protobuf")

    private val sprite: Scene by gameContext.fileHandler.get("proto/sprite.protobuf")

    override fun createEntities(engine: Engine) {
        /*
        val models = scene.children.filter { it.type == ObjectType.MODEL }
        models.forEach { node ->
            val entity = engine.createFromNode(node, gameContext, scene)
            if (node.name.toLowerCase() == "player") {
                entity.add(Player())
            }
        }
    */
        val cameras = scene.children.filter { it.type == ObjectType.CAMERA }
        cameras.forEach { node ->
            engine.createFromNode(node, gameContext, scene)
        }

        val models = scene.children.filter { it.type == ObjectType.MODEL }
        models.forEach { node ->
            engine.createFromNode(node, gameContext, scene)
        }



        val player = scene.children.filter { it.type == ObjectType.BOX }
        player.forEach { _ ->
            val sprite = engine.createSprite(sprite.sprites.values.first(), sprite)
            sprite.remove(Position::class)
            // sprite.add(Position(transformation = node.transformation.toMat4()))
            sprite.add(Position())
            sprite.add(Player())
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(PlayerSystem(gameContext.input)) + super.createSystems(engine)
    }
}


@ExperimentalStdlibApi
class SceneGame(gameContext: GameContext) : GameSystem(gameContext, SceneScreen(gameContext))

