package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.file.Sound
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key

class Player : Component

class PlayerSystem(val inputHandler: InputHandler, val sound: Sound) : System(EntityQuery(TextComponent::class)) {

    private var time = 0f

    override fun update(delta: Seconds, entity: Entity) {
        time += delta
        if (inputHandler.isKeyPressed(Key.ARROW_RIGHT)) {
            entity.position.addLocalRotation(x = 90, delta = delta)
        } else if (inputHandler.isKeyPressed(Key.ARROW_LEFT)) {
            entity.position.addLocalRotation(x = -90, delta = delta)
        }

        if (inputHandler.isKeyPressed(Key.ARROW_DOWN)) {
            entity.position.addLocalRotation(y = 90, delta = delta)
        } else if (inputHandler.isKeyPressed(Key.ARROW_UP)) {
            entity.position.addLocalRotation(y = -90, delta = delta)
        }

        if(inputHandler.isKeyJustPressed(Key.SPACE)) {
            println("play")
            sound.play()
        }
       // entity.position.setScale(x = cos(time) * 3f, y = cos(time) * 3f)
      //  entity.position.setRotationX(cos(time) * 360f)
    }
}

@ExperimentalStdlibApi
class SceneScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("proto/asteroid.protobuf")

    private val sprite: Scene by gameContext.fileHandler.get("proto/sprite.protobuf")

    private val font: Font by gameContext.fileHandler.get("pt_font")

    private val sound: Sound by gameContext.fileHandler.get("shoot.mp3")

    override fun createEntities(engine: Engine) {
        engine.create {
            val meshPrimitive = MeshPrimitive(
                id = Id(),
                name = "undefined",
                material = null,
                texture = font.fontSprite,
                hasAlpha = true,
                primitive = Primitive(
                    id = Id(),
                    materialId = Id.None,
                    vertices = listOf(
                        Vertex(Position(0f, 0f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 0f)),
                        Vertex(Position(1f, 0f, 0f), Normal(0f, 0f, 0f), uv = UV(1f, 0f)),
                        Vertex(Position(0f, 1f, 0f), Normal(0f, 0f, 0f), uv = UV(1f, 1f)),
                        Vertex(Position(1f, 1f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 1f))
                    ),
                    verticesOrder = intArrayOf(
                        0, 1, 2,
                        2, 1, 3
                    )
                )
            )

            val text = TextComponent(
                text = "HelloWorld!",
                font = font,
                meshPrimitive = meshPrimitive
            )
            add(meshPrimitive)
            add(text)
            add(com.github.dwursteisen.minigdx.ecs.components.Position(

            ).addGlobalTranslation(z = -0.5))
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(PlayerSystem(gameContext.input, sound)) + super.createSystems(engine)
    }
}


@ExperimentalStdlibApi
class SceneGame(gameContext: GameContext) : GameSystem(gameContext, SceneScreen(gameContext))

