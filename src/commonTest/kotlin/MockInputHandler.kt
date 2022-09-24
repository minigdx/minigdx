import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.math.ImmutableVector2
import com.github.dwursteisen.minigdx.math.Vector2

class MockInputHandler : InputHandler {
    override fun isKeyJustPressed(key: Key): Boolean {
        TODO("Not yet implemented")
    }

    override fun isKeyPressed(key: Key): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTouched(signal: TouchSignal): Vector2? {
        TODO("Not yet implemented")
    }

    override fun isJustTouched(signal: TouchSignal): Vector2? {
        TODO("Not yet implemented")
    }

    override val currentTouch: ImmutableVector2
        get() = TODO("Not yet implemented")
}
