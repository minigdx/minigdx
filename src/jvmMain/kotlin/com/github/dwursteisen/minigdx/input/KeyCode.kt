package com.github.dwursteisen.minigdx.input

import org.lwjgl.glfw.GLFW

const val UNKNOWN_KEY = 257

val Key.keyCode: Int
    get() {
        return when (this) {
            Key.ANY_KEY -> UNKNOWN_KEY
            Key.BACKSPACE -> GLFW.GLFW_KEY_BACKSPACE
            Key.TAB -> GLFW.GLFW_KEY_TAB
            Key.ENTER -> GLFW.GLFW_KEY_ENTER
            Key.SHIFT -> GLFW.GLFW_KEY_LEFT_SHIFT // FIXME: how to handle right/left ?
            Key.CTRL -> GLFW.GLFW_KEY_LEFT_CONTROL // FIXME: how to handle right/left ?
            Key.ALT -> GLFW.GLFW_KEY_LEFT_ALT // FIXME
            Key.PAUSE_BREAK -> GLFW.GLFW_KEY_PAUSE
            Key.CAPS_LOCK -> GLFW.GLFW_KEY_CAPS_LOCK
            Key.ESCAPE -> GLFW.GLFW_KEY_ESCAPE
            Key.PAGE_UP -> GLFW.GLFW_KEY_PAGE_UP
            Key.SPACE -> GLFW.GLFW_KEY_SPACE
            Key.PAGE_DOWN -> GLFW.GLFW_KEY_PAGE_DOWN
            Key.END -> GLFW.GLFW_KEY_END
            Key.HOME -> GLFW.GLFW_KEY_HOME
            Key.ARROW_LEFT -> GLFW.GLFW_KEY_LEFT
            Key.ARROW_UP -> GLFW.GLFW_KEY_UP
            Key.ARROW_RIGHT -> GLFW.GLFW_KEY_RIGHT
            Key.ARROW_DOWN -> GLFW.GLFW_KEY_DOWN
            Key.PRINT_SCREEN -> GLFW.GLFW_KEY_PRINT_SCREEN
            Key.INSERT -> GLFW.GLFW_KEY_INSERT
            Key.DELETE -> GLFW.GLFW_KEY_DELETE
            Key.NUM0 -> GLFW.GLFW_KEY_0
            Key.NUM1 -> GLFW.GLFW_KEY_1
            Key.NUM2 -> GLFW.GLFW_KEY_2
            Key.NUM3 -> GLFW.GLFW_KEY_3
            Key.NUM4 -> GLFW.GLFW_KEY_4
            Key.NUM5 -> GLFW.GLFW_KEY_5
            Key.NUM6 -> GLFW.GLFW_KEY_6
            Key.NUM7 -> GLFW.GLFW_KEY_7
            Key.NUM8 -> GLFW.GLFW_KEY_8
            Key.NUM9 -> GLFW.GLFW_KEY_9
            Key.A -> GLFW.GLFW_KEY_A
            Key.B -> GLFW.GLFW_KEY_B
            Key.C -> GLFW.GLFW_KEY_C
            Key.D -> GLFW.GLFW_KEY_D
            Key.E -> GLFW.GLFW_KEY_E
            Key.F -> GLFW.GLFW_KEY_F
            Key.G -> GLFW.GLFW_KEY_G
            Key.H -> GLFW.GLFW_KEY_H
            Key.I -> GLFW.GLFW_KEY_I
            Key.J -> GLFW.GLFW_KEY_J
            Key.K -> GLFW.GLFW_KEY_K
            Key.L -> GLFW.GLFW_KEY_L
            Key.M -> GLFW.GLFW_KEY_M
            Key.N -> GLFW.GLFW_KEY_N
            Key.O -> GLFW.GLFW_KEY_O
            Key.P -> GLFW.GLFW_KEY_P
            Key.Q -> GLFW.GLFW_KEY_Q
            Key.R -> GLFW.GLFW_KEY_R
            Key.S -> GLFW.GLFW_KEY_S
            Key.T -> GLFW.GLFW_KEY_T
            Key.U -> GLFW.GLFW_KEY_U
            Key.V -> GLFW.GLFW_KEY_V
            Key.W -> GLFW.GLFW_KEY_W
            Key.X -> GLFW.GLFW_KEY_X
            Key.Y -> GLFW.GLFW_KEY_Y
            Key.Z -> GLFW.GLFW_KEY_Z
            Key.LEFT_WINDOW_KEY -> TODO()
            Key.RIGHT_WINDOW_KEY -> TODO()
            Key.SELECT_KEY -> TODO()
            Key.NUMPAD0 -> TODO()
            Key.NUMPAD1 -> TODO()
            Key.NUMPAD2 -> TODO()
            Key.NUMPAD3 -> TODO()
            Key.NUMPAD4 -> TODO()
            Key.NUMPAD5 -> TODO()
            Key.NUMPAD6 -> TODO()
            Key.NUMPAD7 -> TODO()
            Key.NUMPAD8 -> TODO()
            Key.NUMPAD9 -> TODO()
            Key.MULTIPLY -> TODO()
            Key.ADD -> TODO()
            Key.SUBTRACT -> TODO()
            Key.DECIMAL_POINT -> TODO()
            Key.DIVIDE -> TODO()
            Key.F1 -> GLFW.GLFW_KEY_F1
            Key.F2 -> GLFW.GLFW_KEY_F2
            Key.F3 -> GLFW.GLFW_KEY_F3
            Key.F4 -> GLFW.GLFW_KEY_F4
            Key.F5 -> GLFW.GLFW_KEY_F5
            Key.F6 -> GLFW.GLFW_KEY_F6
            Key.F7 -> GLFW.GLFW_KEY_F7
            Key.F8 -> GLFW.GLFW_KEY_F8
            Key.F9 -> GLFW.GLFW_KEY_F9
            Key.F10 -> GLFW.GLFW_KEY_F10
            Key.F11 -> GLFW.GLFW_KEY_F11
            Key.F12 -> GLFW.GLFW_KEY_F12
            Key.NUM_LOCK -> GLFW.GLFW_KEY_NUM_LOCK
            Key.SCROLL_LOCK -> GLFW.GLFW_KEY_SCROLL_LOCK
            Key.MY_COMPUTER -> TODO()
            Key.MY_CALCULATOR -> TODO()
            Key.SEMI_COLON -> TODO()
            Key.EQUAL_SIGN -> TODO()
            Key.COMMA -> GLFW.GLFW_KEY_COMMA
            Key.DASH -> TODO()
            Key.PERIOD -> GLFW.GLFW_KEY_PERIOD
            Key.FORWARD_SLASH -> TODO()
            Key.OPEN_BRACKET -> TODO()
            Key.BACK_SLASH -> TODO()
            Key.CLOSE_BRAKET -> TODO()
            Key.SINGLE_QUOTE -> TODO()
        }
    }
