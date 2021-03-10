package demo

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.ecs.systems.TemporalSystem
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.math.Interpolations
import kotlin.random.Random

class ObjectWithRotation(
    var amount: Int = 0,
    var actual: Boolean = false,
    val scale: Float3
) : Component

class Countdown(var time: Seconds = 20f) : Component
class NameComponent(var name: String = "") : Component

/**
 * Make object turn
 */
class RotationObject : System(EntityQuery(ObjectWithRotation::class)) {

    val name by interested(EntityQuery(NameComponent::class))

    override fun update(delta: Seconds, entity: Entity) {
        val objectWithRotation = entity.get(ObjectWithRotation::class)
        val position = entity.get(Position::class)

        if (objectWithRotation.actual) {
            position.addLocalRotation(y = 90f, delta = delta)
            position.setGlobalScale(
                x = Interpolations.lerp(objectWithRotation.scale.x, position.scale.x, 0.8f),
                y = Interpolations.lerp(objectWithRotation.scale.y, position.scale.y, 0.8f),
                z = Interpolations.lerp(objectWithRotation.scale.z, position.scale.z, 0.8f)
            )

            position.setGlobalTranslation(
                x = Interpolations.lerp(0f, position.globalTranslation.x),
                y = Interpolations.lerp(0f, position.globalTranslation.y),
                z = Interpolations.lerp(0f, position.globalTranslation.z)
            )

            name.forEach {
                it.get(TextComponent::class).text = "Price: $" + entity.get(ObjectWithRotation::class).amount.toString()

            }
        } else {
            position.setGlobalScale(
                x = Interpolations.lerp(0f, position.scale.x, 0.8f),
                y = Interpolations.lerp(0f, position.scale.y, 0.8f),
                z = Interpolations.lerp(0f, position.scale.z, 0.8f)
            )
            position.setGlobalTranslation(
                x = Interpolations.lerp(0f, position.globalTranslation.x),
                y = Interpolations.lerp(-2f, position.globalTranslation.y),
                z = Interpolations.lerp(0f, position.globalTranslation.z)
            )
        }
    }
}

class UserText(var amount: Int = 0) : Component

class StopTheGame : Event
class LooseTheGame : Event
class NextObject : Event
class ValidateObject : Event
class UserIsWrong : Event

class UserTextSystem : System(EntityQuery(UserText::class)) {

    var isOver = false
    var isWrong = 0f

    override fun update(delta: Seconds, entity: Entity) {
        isWrong -= delta

        var amount = entity.get(UserText::class).amount
        if (input.isKeyJustPressed(Key.ESCAPE)) {
            emit(StopTheGame())
        } else if (input.isKeyJustPressed(Key.NUM0)) {
            amount *= 10
        } else if (input.isKeyJustPressed(Key.NUM1)) {
            amount *= 10
            amount += 1
        } else if (input.isKeyJustPressed(Key.NUM2)) {
            amount *= 10
            amount += 2
        } else if (input.isKeyJustPressed(Key.NUM3)) {
            amount *= 10
            amount += 3
        } else if (input.isKeyJustPressed(Key.NUM4)) {
            amount *= 10
            amount += 4
        } else if (input.isKeyJustPressed(Key.NUM5)) {
            amount *= 10
            amount += 5
        } else if (input.isKeyJustPressed(Key.NUM6)) {
            amount *= 10
            amount += 6
        } else if (input.isKeyJustPressed(Key.NUM7)) {
            amount *= 10
            amount += 7
        } else if (input.isKeyJustPressed(Key.NUM8)) {
            amount *= 10
            amount += 8
        } else if (input.isKeyJustPressed(Key.NUM9)) {
            amount *= 10
            amount += 9
        } else if (input.isKeyJustPressed(Key.BACKSPACE)) {
            if(amount != 0) {
                amount /= 10
            }
        } else if (input.isKeyJustPressed(Key.SPACE)) {
            emit(NextObject())
        } else if (input.isKeyJustPressed(Key.ENTER)) {
            emit(ValidateObject())
        }

        if(!isOver) {
            entity.get(TextComponent::class).text = "$ " +amount.toString()
        }

        if(isWrong > 0f) {
            entity.get(TextComponent::class).text = "WRONG !"
        }
        entity.get(UserText::class).amount = amount
    }

    override fun onEvent(event: Event, entityQuery: EntityQuery?) {
        if(event is LooseTheGame || event is StopTheGame) {
            isOver = true
        }

        if(event is UserIsWrong) {
            isWrong = 0.5f
        }
    }
}

class CountdownSystem : TemporalSystem(0.01f, EntityQuery(Countdown::class)) {

    var enabled = true
    override fun update(delta: Seconds, entity: Entity) {
        if (!enabled) return

        entity.get(Countdown::class).time -= delta
        entity.get(TextComponent::class).text = String.format("%.2f", entity.get(Countdown::class).time) + " secs"

        if(entity.get(Countdown::class).time < 0f) {
            emit(LooseTheGame())
        }
    }

    override fun onEvent(event: Event, entityQuery: EntityQuery?) {
        if (event is StopTheGame) {
            enabled = false
        } else if(event is LooseTheGame) {
            enabled = false
            entities.forEach {
                it.get(Countdown::class).time = 0f

            }
        }
    }

    override fun timeElapsed() = Unit
}

class ObjectPicker : System(EntityQuery()) {

    var score = 0

    val objects by interested(EntityQuery(ObjectWithRotation::class))

    val userInput by interested(EntityQuery(UserText::class))

    override fun update(delta: Seconds, entity: Entity) = Unit

    override fun onGameStarted(engine: Engine) {
        emit(NextObject())
    }

    override fun onEvent(event: Event, entityQuery: EntityQuery?) {
        when (event) {
            is NextObject -> {
                val objectWithRotation = objects
                    .filterNot { it.get(ObjectWithRotation::class).actual }.random()
                    .get(ObjectWithRotation::class)
                objects.forEach { it.get(ObjectWithRotation::class).actual = false }
                objectWithRotation.actual = true
                objectWithRotation.amount = Random.nextInt(123, 5523)
                userInput.forEach {
                    it.get(UserText::class).amount = 0
                }
            }
            is ValidateObject -> {
                if (userInput.first()
                        .get(UserText::class).amount == objects.first { it.get(ObjectWithRotation::class).actual }
                        .get(ObjectWithRotation::class).amount
                ) {
                    score++
                    emit(NextObject())
                } else {
                    emit(UserIsWrong())
                }
            }
            is LooseTheGame -> {
                score = 0
                userInput.first()
                    .get(TextComponent::class).text = "You Loose..."
            }

            is StopTheGame -> {
                userInput.first()
                    .get(TextComponent::class).text = "You won $score objs"
            }
        }
    }
}

@ExperimentalStdlibApi
class TrijamGame(override val gameContext: GameContext) : Game {

    val scene: Scene by gameContext.fileHandler.get("v2/trijam.protobuf")

    val text: Font by gameContext.fileHandler.get("pt_font")

    override fun createEntities(entityFactory: EntityFactory) {
        scene.children.forEach {
            if (it.type == ObjectType.BOX) {
                val transformation = Mat4.fromColumnMajor(*it.transformation.matrix)
                val e = entityFactory.createText(" ", text, transformation)
                if (it.name == "countdown") {
                    e.add(Countdown())
                } else if (it.name == "text") {

                    e.add(UserText())
                } else {
                    e.add(NameComponent())
                }
            } else if (it.name.startsWith("obj")) {
                entityFactory.createFromNode(it, scene).apply {
                    this.add(
                        ObjectWithRotation(
                            scale = Mat4.fromColumnMajor(*it.transformation.matrix).scale
                        )
                    )
                }
            } else {
                entityFactory.createFromNode(it, scene)
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return super.createSystems(engine) + listOf(
            RotationObject(),
            CountdownSystem(),
            UserTextSystem(),
            ObjectPicker()
        )
    }
}
