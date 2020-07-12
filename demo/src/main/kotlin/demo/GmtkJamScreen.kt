package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.abs

fun lerp(target: Float, current: Float, step: Float = 0.9f): Float {
    return target + step * (current - target)
}

class Player(
    val spawn: Vector3,
    val position: Vector3 = Vector3(),
    var moving: Boolean = false
) : Component

class PlayerSystem(val input: InputHandler) : System(EntityQuery(Player::class)) {

    private val moveFactor = 2f

    override fun update(delta: Seconds, entity: Entity) {
        val player = entity.get(Player::class)
        // The player is not moving. It can move again
        if (!player.moving) {
            if (input.isKeyJustPressed(Key.ARROW_LEFT)) {
                player.position.x--
            } else if (input.isKeyJustPressed(Key.ARROW_RIGHT)) {
                player.position.x++
            } else if (input.isKeyJustPressed(Key.ARROW_UP)) {
                player.position.z--
            } else if (input.isKeyJustPressed(Key.ARROW_DOWN)) {
                player.position.z++
            }
        }

        val position = entity.get(Position::class)
        val newX = lerp(
            target = player.position.x * moveFactor + player.spawn.x,
            current = position.translation.x,
            step = 0.9f
        )
        val newZ = lerp(
            target = player.position.z * moveFactor + player.spawn.z,
            current = position.translation.z,
            step = 0.9f
        )
        player.moving = !(isCloseTo(position.translation.x, newX) && isCloseTo(position.translation.z, newZ))
        position.setTranslate(
            x = newX,
            z = newZ
        )
    }

    private fun isCloseTo(expected: Float, actual: Float, delta: Float = 0.01f): Boolean {
        return abs(expected - actual) <= delta
    }
}

@ExperimentalStdlibApi
class GmtkJamScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("v2/gmtkjam.protobuf")

    override fun createEntities(engine: Engine) {
        scene.models.values.forEach {
            val entity = engine.createFrom(it, scene, gameContext)
            if (it.name == "player") {
                val spawn = entity.get(Position::class).translation
                entity.add(Player(spawn = spawn.copy()))
            }
        }

        scene.orthographicCameras["Camera"]!!.let {
            engine.createFrom(it, gameContext)
        }
    }

    override fun createSystems(): List<System> {
        return listOf(PlayerSystem(gameContext.input))
    }
}
