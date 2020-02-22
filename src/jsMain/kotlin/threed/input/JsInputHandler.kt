package threed.input

import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import threed.math.Vector2
import kotlin.browser.document

class JsInputHandler : InputHandler, InputManager {

    init {
        document.addEventListener("keydown", ::keyUp, false)
        document.addEventListener("keyup", ::keyDown, false)
    }

    private val keys: Array<Boolean> = Array<Boolean>(256 + 1) { false }
    private val pressed: Array<Boolean> = Array<Boolean>(256 + 1) { false }

    private fun keyUp(event: Event) {
        event as KeyboardEvent
        if (event.keyCode in (0..256)) {
            keys[event.keyCode] = true
            pressed[event.keyCode] = true
        }
    }

    private fun keyDown(event: Event) {
        event as KeyboardEvent
        if (event.keyCode in (0..256)) {
            keys[event.keyCode] = false
        }
    }

    override fun isKey(key: Key): Boolean = keys[key.keyCode]

    override fun isKeyPressed(key: Key): Boolean = pressed[key.keyCode]

    override fun isTouched(signal: TouchSignal): Vector2? = null

    override fun isJustTouched(signal: TouchSignal): Vector2? = null

    override fun record() = Unit

    override fun reset() {
        (pressed.indices).forEach {
            pressed[it] = false
        }
    }
}
