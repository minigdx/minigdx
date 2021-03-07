package com.github.dwursteisen.minigdx.input

const val UNKNOWN_KEY = 257

val Key.keyCode: Int
    get() {
        return when (this) {
            Key.ANY_KEY -> UNKNOWN_KEY
            Key.BACKSPACE -> 8
            Key.TAB -> 9
            Key.ENTER -> 13
            Key.SHIFT -> TODO()
            Key.CTRL -> TODO()
            Key.ALT -> TODO()
            Key.PAUSE_BREAK -> TODO()
            Key.CAPS_LOCK -> TODO()
            Key.ESCAPE -> 27
            Key.PAGE_UP -> TODO()
            Key.SPACE -> 32
            Key.PAGE_DOWN -> TODO()
            Key.END -> TODO()
            Key.HOME -> TODO()
            Key.ARROW_LEFT -> 37
            Key.ARROW_UP -> 38
            Key.ARROW_RIGHT -> 39
            Key.ARROW_DOWN -> 40
            Key.PRINT_SCREEN -> TODO()
            Key.INSERT -> TODO()
            Key.DELETE -> TODO()
            Key.NUM0 -> 48 + 0
            Key.NUM1 -> 48 + 1
            Key.NUM2 -> 48 + 2
            Key.NUM3 -> 48 + 3
            Key.NUM4 -> 48 + 4
            Key.NUM5 -> 48 + 5
            Key.NUM6 -> 48 + 6
            Key.NUM7 -> 48 + 7
            Key.NUM8 -> 48 + 8
            Key.NUM9 -> 48 + 9
            Key.A -> 65 + 0
            Key.B -> 65 + 1
            Key.C -> 65 + 2
            Key.D -> 65 + 3
            Key.E -> 65 + 4
            Key.F -> 65 + 5
            Key.G -> 65 + 6
            Key.H -> 65 + 7
            Key.I -> 65 + 8
            Key.J -> 65 + 9
            Key.K -> 65 + 10
            Key.L -> 65 + 11
            Key.M -> 65 + 12
            Key.N -> 65 + 13
            Key.O -> 65 + 14
            Key.P -> 65 + 15
            Key.Q -> 65 + 16
            Key.R -> 65 + 17
            Key.S -> 65 + 18
            Key.T -> 65 + 19
            Key.U -> 65 + 20
            Key.V -> 65 + 21
            Key.W -> 65 + 22
            Key.X -> 65 + 23
            Key.Y -> 65 + 24
            Key.Z -> 65 + 25
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
