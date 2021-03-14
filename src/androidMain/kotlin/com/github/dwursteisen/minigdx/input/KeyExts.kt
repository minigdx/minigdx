package com.github.dwursteisen.minigdx.input

import android.view.KeyEvent

val Key.keyCode: Int
    get() {
        return when (this) {
            Key.ANY_KEY -> KeyEvent.KEYCODE_UNKNOWN
            Key.BACKSPACE -> KeyEvent.KEYCODE_BACK
            Key.TAB -> KeyEvent.KEYCODE_TAB
            Key.ENTER -> KeyEvent.KEYCODE_ENTER
            Key.SHIFT -> TODO()
            Key.CTRL -> TODO()
            Key.ALT -> TODO()
            Key.PAUSE_BREAK -> TODO()
            Key.CAPS_LOCK -> TODO()
            Key.ESCAPE -> TODO()
            Key.PAGE_UP -> TODO()
            Key.SPACE -> KeyEvent.KEYCODE_SPACE
            Key.PAGE_DOWN -> TODO()
            Key.END -> TODO()
            Key.HOME -> TODO()
            Key.ARROW_LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            Key.ARROW_UP -> KeyEvent.KEYCODE_DPAD_UP
            Key.ARROW_RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
            Key.ARROW_DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
            Key.PRINT_SCREEN -> TODO()
            Key.INSERT -> TODO()
            Key.DELETE -> TODO()
            Key.NUM0 -> KeyEvent.KEYCODE_NUMPAD_0
            Key.NUM1 -> KeyEvent.KEYCODE_NUMPAD_1
            Key.NUM2 -> KeyEvent.KEYCODE_NUMPAD_2
            Key.NUM3 -> KeyEvent.KEYCODE_NUMPAD_3
            Key.NUM4 -> KeyEvent.KEYCODE_NUMPAD_4
            Key.NUM5 -> KeyEvent.KEYCODE_NUMPAD_5
            Key.NUM6 -> KeyEvent.KEYCODE_NUMPAD_6
            Key.NUM7 -> KeyEvent.KEYCODE_NUMPAD_7
            Key.NUM8 -> KeyEvent.KEYCODE_NUMPAD_8
            Key.NUM9 -> KeyEvent.KEYCODE_NUMPAD_9
            Key.A -> KeyEvent.KEYCODE_A
            Key.B -> KeyEvent.KEYCODE_B
            Key.C -> KeyEvent.KEYCODE_C
            Key.D -> KeyEvent.KEYCODE_D
            Key.E -> KeyEvent.KEYCODE_E
            Key.F -> KeyEvent.KEYCODE_F
            Key.G -> KeyEvent.KEYCODE_G
            Key.H -> KeyEvent.KEYCODE_H
            Key.I -> KeyEvent.KEYCODE_I
            Key.J -> KeyEvent.KEYCODE_J
            Key.K -> KeyEvent.KEYCODE_K
            Key.L -> KeyEvent.KEYCODE_L
            Key.M -> KeyEvent.KEYCODE_M
            Key.N -> KeyEvent.KEYCODE_N
            Key.O -> KeyEvent.KEYCODE_O
            Key.P -> KeyEvent.KEYCODE_P
            Key.Q -> KeyEvent.KEYCODE_Q
            Key.R -> KeyEvent.KEYCODE_R
            Key.S -> KeyEvent.KEYCODE_S
            Key.T -> KeyEvent.KEYCODE_T
            Key.U -> KeyEvent.KEYCODE_U
            Key.V -> KeyEvent.KEYCODE_V
            Key.W -> KeyEvent.KEYCODE_W
            Key.X -> KeyEvent.KEYCODE_X
            Key.Y -> KeyEvent.KEYCODE_Y
            Key.Z -> KeyEvent.KEYCODE_Z
            Key.LEFT_WINDOW_KEY -> TODO()
            Key.RIGHT_WINDOW_KEY -> TODO()
            Key.SELECT_KEY -> KeyEvent.KEYCODE_DPAD_CENTER
            Key.NUMPAD0 -> KeyEvent.KEYCODE_NUMPAD_0
            Key.NUMPAD1 -> KeyEvent.KEYCODE_NUMPAD_1
            Key.NUMPAD2 -> KeyEvent.KEYCODE_NUMPAD_2
            Key.NUMPAD3 -> KeyEvent.KEYCODE_NUMPAD_3
            Key.NUMPAD4 -> KeyEvent.KEYCODE_NUMPAD_4
            Key.NUMPAD5 -> KeyEvent.KEYCODE_NUMPAD_5
            Key.NUMPAD6 -> KeyEvent.KEYCODE_NUMPAD_6
            Key.NUMPAD7 -> KeyEvent.KEYCODE_NUMPAD_7
            Key.NUMPAD8 -> KeyEvent.KEYCODE_NUMPAD_8
            Key.NUMPAD9 -> KeyEvent.KEYCODE_NUMPAD_9
            Key.MULTIPLY -> TODO()
            Key.ADD -> TODO()
            Key.SUBTRACT -> TODO()
            Key.DECIMAL_POINT -> TODO()
            Key.DIVIDE -> TODO()
            Key.F1 -> TODO()
            Key.F2 -> TODO()
            Key.F3 -> TODO()
            Key.F4 -> TODO()
            Key.F5 -> TODO()
            Key.F6 -> TODO()
            Key.F7 -> TODO()
            Key.F8 -> TODO()
            Key.F9 -> TODO()
            Key.F10 -> TODO()
            Key.F11 -> TODO()
            Key.F12 -> TODO()
            Key.NUM_LOCK -> TODO()
            Key.SCROLL_LOCK -> TODO()
            Key.MY_COMPUTER -> TODO()
            Key.MY_CALCULATOR -> TODO()
            Key.SEMI_COLON -> TODO()
            Key.EQUAL_SIGN -> TODO()
            Key.COMMA -> TODO()
            Key.DASH -> TODO()
            Key.PERIOD -> TODO()
            Key.FORWARD_SLASH -> TODO()
            Key.OPEN_BRACKET -> TODO()
            Key.BACK_SLASH -> TODO()
            Key.CLOSE_BRAKET -> TODO()
            Key.SINGLE_QUOTE -> TODO()
        }
    }