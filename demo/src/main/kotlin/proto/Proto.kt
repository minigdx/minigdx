package proto

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.StateMachineComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.physics.AABBCollisionResolver
import com.github.dwursteisen.minigdx.ecs.states.State
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.StateMachineSystem
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.GameWrapper
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.math.Interpolations.lerp

class Player(val input: InputHandler) : StateMachineComponent()

class Cube(val value: Int) : StateMachineComponent()

class Zone(var subEntity: Entity) : StateMachineComponent()

class Circle : Component

class PickItemEvent : Event

class ReleaseItemEvent : Event

class ZoneSystem(logger: Logger) : StateMachineSystem(Zone::class, logger) {

    private val players by interested(EntityQuery(Player::class))
    private val circles by interested(EntityQuery(Circle::class))

    private val collider = AABBCollisionResolver()

    class Wait(private val system: ZoneSystem) : State() {

        override fun configure() {
            onEvent(PickItemEvent::class) { _ ->
                WaitOn(system)
            }
        }
    }

    class WaitOn(private val system: ZoneSystem) : State() {

        private lateinit var circle: Entity

        private var time = 0f

        override fun onEnter(entity: Entity) {
            val circleEntity = system.circles.first()
            circle = system.create {
                add(circleEntity.components.filter { it::class != Position::class && it::class != Circle::class })
                add(Position())
            }
            val position = entity.get(Position::class).translation
            circle.get(Position::class)
                .setGlobalTranslation(position.x, position.y + 0.1f, position.z)
        }

        override fun onExit(entity: Entity) {
            circle.destroy()
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            time += delta
            val circleScale = abs(sin(time))
            circle.get(Position::class).setScale(circleScale, 1f, circleScale)
            return if (system.collider.collide(entity, system.players.first())) {
                TurnOn(system)
            } else {
                null
            }
        }
    }

    class TurnOn(private val system: ZoneSystem) : State() {
        var time = 0f
        var y = 0f

        override fun onEnter(entity: Entity) {
            val sub = entity.get(Zone::class).subEntity
            y = sub.get(Position::class).translation.y
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            val sub = entity.get(Zone::class).subEntity
            return if (system.collider.collide(entity, system.players.first())) {
                time += delta
                sub.get(Position::class).setGlobalTranslation(y = y - abs(cos(time * 3f)) * 0.05f)
                null
            } else {
                WaitOn(system)
            }
        }

        override fun onExit(entity: Entity) {
            val sub = entity.get(Zone::class).subEntity
            sub.get(Position::class).setGlobalTranslation(y = y)
        }
    }

    override fun initialState(entity: Entity): State = Wait(this)

}

class CubeSystem(logger: Logger) : StateMachineSystem(Cube::class, logger) {

    private val collider = AABBCollisionResolver()

    private val players by interested(EntityQuery(Player::class))
    private val zones by interested(EntityQuery(Zone::class))

    class Wait(val system: CubeSystem) : State() {

        private var y = 0f

        private var time = 0f

        private var waitOnZone: Boolean = false

        override fun onEnter(entity: Entity) {
            y = entity.get(Position::class).translation.y
            waitOnZone = system.zones.any { system.collider.collide(it, entity) }
        }

        override fun onExit(entity: Entity) {
            entity.get(Position::class).setGlobalTranslation(y = y)
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            if (waitOnZone) {
                return WaitOnZone(system)
            }
            time += delta
            entity.get(Position::class).setGlobalTranslation(y = y + abs(cos(time * 3f)) * 0.5f)

            val player = system.players.first()
            return if (system.collider.collide(player, entity)) {
                Touch(system)
            } else {
                null
            }
        }
    }

    class Touch(private val system: CubeSystem) : State() {

        private var rotation: Float = 0f

        override fun onEnter(entity: Entity) {
            rotation = entity.get(Position::class).rotation.z
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            entity.get(Position::class).addLocalRotation(y = 360f, delta = delta)
            val player = system.players.first()
            return if (player.get(Player::class).input.isKeyJustPressed(Key.SPACE)) {
                Move(system)
            } else if (!system.collider.collide(player, entity)) {
                Wait(system)
            } else {
                null
            }
        }

        override fun onExit(entity: Entity) {
            entity.get(Position::class).setLocalRotation(y = rotation)
        }
    }

    class Move(private val system: CubeSystem) : State() {

        private val offset = translation(Float3(0f, 0f, 0.5f))

        override fun onEnter(entity: Entity) {
            emitEvents(PickItemEvent())
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            val player = system.players.first()

            val translation = player.get(Position::class).transformation.translation
            val target = (offset * translation(Float3(translation.x, translation.y, translation.z)))
                .position

            val position = entity.get(Position::class)
            position
                .setGlobalTranslation(
                    x = lerp(target.x, position.translation.x),
                    z = lerp(target.z, position.translation.z)
                )

            return if (player.get(Player::class).input.isKeyJustPressed(Key.SPACE)) {
                Wait(system)
            } else {
                null
            }
        }

        override fun onExit(entity: Entity) {
            emitEvents(ReleaseItemEvent())
        }
    }

    class WaitOnZone(val system: CubeSystem) : State() {
        override fun update(delta: Seconds, entity: Entity): State? {
            println("wait")
            return null
        }
    }

    override fun initialState(entity: Entity): State {
        return Wait(this)
    }
}

class PlayerSystem(private val inputHandler: InputHandler, logger: Logger) :
    StateMachineSystem(Player::class, logger) {

    private class Waiting(private val inputHandler: InputHandler) : State() {
        override fun configure() = Unit

        override fun update(delta: Seconds, entity: Entity): State? {
            return if (inputHandler.isAnyKeysPressed(
                    Key.ARROW_RIGHT,
                    Key.ARROW_LEFT,
                    Key.ARROW_DOWN,
                    Key.ARROW_UP
                )
            ) {
                Move(inputHandler)
            } else {
                null
            }
        }
    }

    class Move(private val inputHandler: InputHandler) : State() {
        override fun configure() = Unit

        override fun update(delta: Seconds, entity: Entity): State? {
            var isKeyPressed = false
            if (inputHandler.isKeyPressed(Key.ARROW_UP)) {
                entity.get(Position::class).addLocalTranslation(z = MOVE_SPEED, delta = delta)
                isKeyPressed = true
            } else if (inputHandler.isKeyPressed(Key.ARROW_DOWN)) {
                entity.get(Position::class).addLocalTranslation(z = -MOVE_SPEED, delta = delta)
                isKeyPressed = true
            }

            if (inputHandler.isKeyPressed(Key.ARROW_LEFT)) {
                entity.get(Position::class).addLocalTranslation(x = MOVE_SPEED, delta = delta)
                isKeyPressed = true
            } else if (inputHandler.isKeyPressed(Key.ARROW_RIGHT)) {
                entity.get(Position::class).addLocalTranslation(x = -MOVE_SPEED, delta = delta)
                isKeyPressed = true
            }

            return if (isKeyPressed) {
                null
            } else {
                Waiting(inputHandler)
            }
        }

        companion object {
            const val MOVE_SPEED = 4f
        }
    }

    override fun initialState(entity: Entity): State {
        return Waiting(inputHandler)
    }
}

@ExperimentalStdlibApi
class ProtoGame(override val gameContext: GameContext) : Game {

    private val assets by gameContext.fileHandler.get<Scene>("proto/assets.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        val arena = assets.children.first { node -> node.name == "arena" }
        entityFactory.createFromNode(assets.children.first { it.type == ObjectType.CAMERA }, assets)
        entityFactory.createFromNode(arena, assets)
        val origin = Mat4.fromColumnMajor(*arena.transformation.matrix)
        arena.children.forEach { node ->
            val fromColumnMajor = node.transformation.toMat4()
            when (node.name) {
                "player_spawn" -> {
                    val entity = entityFactory.createFromNode(
                        node,
                        assets,
                        transformation = origin * fromColumnMajor
                    )
                    entity.add(Player(gameContext.input))
                }
                "cube_one" -> {
                    val entity = entityFactory.createFromNode(
                        node,
                        assets,
                        transformation = origin * fromColumnMajor
                    )
                    entity.add(Cube(1))
                }
                "zone_detection" -> {
                    val sub = node.children.first()
                    val subEntity = entityFactory.createFromNode(
                        sub,
                        assets,
                        transformation = origin * fromColumnMajor * Mat4.fromColumnMajor(*sub.transformation.matrix)
                    )
                    entityFactory.create {
                        val box = BoundingBox.from(node.transformation.toMat4())
                        add(box)
                        add(Position(transformation = origin * fromColumnMajor))
                        add(Zone(subEntity))
                    }
                }
            }
        }
        val circle = assets.children.first { it.name == "circle" }
        entityFactory.createModel(
            circle,
            assets,
            transformation = Mat4.identity()
        ).add(Circle())
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(
            PlayerSystem(gameContext.input, gameContext.logger),
            CubeSystem(gameContext.logger),
            ZoneSystem(gameContext.logger)
        ) + super.createSystems(engine)
    }
}
