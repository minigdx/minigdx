package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.game.Screen

@ExperimentalStdlibApi
class GmtkJamScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("v2/gmtkjam.protobuf")

    override fun createEntities(engine: Engine) {
        scene.models.values.forEach {
            engine.createFrom(it, scene, gameContext)
        }

        scene.orthographicCameras["Camera"]!!.let {
            engine.createFrom(it, gameContext)
        }
    }
}
