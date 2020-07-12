package demo

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.translation
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
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.abs
import kotlin.math.round

fun lerp(target: Float, current: Float, step: Float = 0.9f): Float {
    return target + step * (current - target)
}

class Player(
    val spawn: Vector3,
    val levelPosition: Vector3,
    val position: Vector3 = Vector3(),
    var moving: Boolean = false,
    var restart: Boolean = false,
    var mapping: Map<Key, Key> = emptyMap(),
    var move: Int = 0,
    val allMappings: Map<Int, Map<Key, Key>> = (0..100).map {
        it to randomKeys()
    }.toMap()
) : Component

class PlayerSystem(val input: InputHandler) : System(EntityQuery(Player::class)) {

    private val moveFactor = 4f

    private val currentLevel by interested(EntityQuery(Level::class))

    override fun update(delta: Seconds, entity: Entity) {
        val player = entity.get(Player::class)
        val level = currentLevel.first().get(Level::class)
        // The player is not moving. It can move again
        if (!player.moving) {
            if (input.isKeyJustPressed(player.mapping[Key.ARROW_LEFT]!!)) {
                player.position.x--
                player.levelPosition.x--
            } else if (input.isKeyJustPressed(player.mapping[Key.ARROW_RIGHT]!!)) {
                player.position.x++
                player.levelPosition.x++
            } else if (input.isKeyJustPressed(player.mapping[Key.ARROW_UP]!!)) {
                player.position.z--
                player.levelPosition.y++
            } else if (input.isKeyJustPressed(player.mapping[Key.ARROW_DOWN]!!)) {
                player.position.z++
                player.levelPosition.y--
            }
        }

        if (input.isKeyJustPressed(Key.R)) {
            restartLevel(player, level)
        }

        val position = entity.get(Position::class)
        val newX = lerp(
            target = player.position.x * moveFactor + player.spawn.x,
            current = position.translation.x,
            step = 0.8f
        )
        val newZ = lerp(
            target = player.position.z * moveFactor + player.spawn.z,
            current = position.translation.z,
            step = 0.8f
        )
        val wasMoving = player.moving
        player.moving = !(isCloseTo(position.translation.x, newX) && isCloseTo(position.translation.z, newZ))

        // move finished
        if (!player.moving && wasMoving) {
            println(level[player.levelPosition.x, player.levelPosition.y])
            player.mapping = player.allMappings[++player.move]!!
            if(level[player.levelPosition.x, player.levelPosition.y] == Tile.HOLE) {
                restartLevel(player, level)
            }
        }

        if (player.restart && !player.moving) {
            // TODO: reset everything
            player.restart = false
        }

        position.setTranslate(
            x = newX,
            z = newZ
        )
    }

    private fun restartLevel(player: Player, level: Level) {
        player.restart = true
        player.position.x = 0f
        player.position.y = 0f
        player.position.z = 0f
        player.levelPosition.x = level.spawn.x
        player.levelPosition.y = level.spawn.y
        player.move = 0
    }

    private fun isCloseTo(expected: Float, actual: Float, delta: Float = 0.01f): Boolean {
        return abs(expected - actual) <= delta
    }
}

enum class Tile {
    HOLE,
    TARGET,
    SPAWN,
    LEFT,
    RIGHT,
    UP,
    DOWN,
    GROUND
}

class Level(private val playground: Array<Array<Int>>) : Component {

    operator fun get(x: Number, y: Number): Tile {
        val yy = round(y.toFloat()).toInt()
        if (yy < 0 || yy >= playground.size) {
            return Tile.HOLE
        }
        val row = playground[playground.size - 1 - yy]
        val xx = round(x.toFloat()).toInt()
        if (xx < 0 || xx >= row.size) {
            return Tile.HOLE
        }
        println("x / y = $xx $yy")
        val value = row[xx]
        return when (value) {
            0 -> Tile.HOLE
            -1 -> Tile.TARGET
            -2 -> Tile.SPAWN
            4 -> Tile.LEFT
            5 -> Tile.RIGHT
            6 -> Tile.UP
            7 -> Tile.DOWN
            1 -> Tile.GROUND
            else -> TODO("not supported")
        }
    }

    val spawn: Vector3 = scan(Tile.SPAWN)!!

    fun scan(tile: Tile): Vector3? {
        playground.indices.forEach { y ->
            playground[y].indices.forEach { x ->
                if (this[x, y] == tile) {
                    return Vector3(x, y, 0)
                }
            }
        }
        return null
    }
}

// 0 = trou ; -1 = target ; -2 = spawn ; 4 = left ; 5 = right ; 6 = up ; 7 = down ; 1 = ground
private val level1 = Level(
    arrayOf(
        arrayOf(0, -1, 0),
        arrayOf(1, 1, 0),
        arrayOf(1, 0, 0),
        arrayOf(1, 1, 1),
        arrayOf(0, -2, 1)
    )
)

fun randomKeys(): Map<Key, Key> {
    val allKeys = mutableListOf<Key>(Key.ARROW_RIGHT, Key.ARROW_LEFT, Key.ARROW_UP, Key.ARROW_DOWN)
    val a = allKeys.random()
    allKeys.remove(a)

    val b = allKeys.random()
    allKeys.remove(b)

    val c = allKeys.random()
    allKeys.remove(c)

    val d = allKeys.random()
    allKeys.remove(d)

    return mapOf(
        Key.ARROW_RIGHT to a,
        Key.ARROW_LEFT to b,
        Key.ARROW_DOWN to c,
        Key.ARROW_UP to d
    )
}
@ExperimentalStdlibApi
class GmtkJamScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("v2/gmtkjam.protobuf")

    override fun createEntities(engine: Engine) {
        log.info("CREATE_ENTITIES") { "Create the level" }
        engine.create {
            add(level1)
        }
        log.info("CREATE_ENTITIES") { "Create the player" }
        scene.models.getValue("player").let {
            val entity = engine.createFrom(it, scene, gameContext)
            val spawn = entity.get(Position::class).translation
            entity.add(Player(
                spawn = spawn.copy(),
                levelPosition = level1.spawn.copy(),
                mapping = randomKeys()
            ))
        }

        log.info("CREATE_ENTITIES") { "Create the play ground" }
        scene.models.filterKeys { it.startsWith("ground") }.values.forEach {
            engine.createFrom(it, scene, gameContext)
        }

        log.info("CREATE_ENTITIES") { "Create the camera" }
        scene.orthographicCameras.getValue("Camera").let {
            engine.createFrom(it, gameContext)
        }
        log.info("CREATE_ENTITIES") { "Create the flag" }
        scene.models.getValue("drapeau").let { model ->
            val entity = engine.createFrom(model, scene, gameContext)
            val target = level1.scan(Tile.TARGET)!!
            entity.remove(Position::class)
            entity.add(
                Position(
                    translation(Float3(0f, 0.125f, -4f)) * translation(Float3((target.x) * 2f - 1.5f, 0f, target.y * -2f) + 1.5f)
                )
            )

        }
    }

    override fun createSystems(): List<System> {
        return listOf(PlayerSystem(gameContext.input))
    }
}
