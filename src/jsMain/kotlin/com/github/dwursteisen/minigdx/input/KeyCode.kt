package com.github.dwursteisen.minigdx.input

const val UNKNOWN_KEY = 257

val Key.keyCode: Int
    get() {
        return when (this) {
            Key.ANY_KEY -> UNKNOWN_KEY
            Key.BACKSPACE -> 8
            Key.TAB -> 9
            Key.ENTER -> 13
            Key.SHIFT -> 16
            Key.CTRL -> 17
            Key.ALT -> 18
            Key.PAUSE_BREAK -> 19
            Key.CAPS_LOCK -> 20
            Key.ESCAPE -> 27
            Key.PAGE_UP -> 33
            Key.SPACE -> 32
            Key.PAGE_DOWN -> 34
            Key.END -> 35
            Key.HOME -> 36
            Key.ARROW_LEFT -> 37
            Key.ARROW_UP -> 38
            Key.ARROW_RIGHT -> 39
            Key.ARROW_DOWN -> 40
            Key.PRINT_SCREEN -> 44
            Key.INSERT -> 45
            Key.DELETE -> 46
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
            Key.LEFT_WINDOW_KEY -> 91
            Key.RIGHT_WINDOW_KEY -> 92
            Key.SELECT_KEY -> 93
            Key.NUMPAD0 -> 96 + 0
            Key.NUMPAD1 -> 96 + 1
            Key.NUMPAD2 -> 96 + 2
            Key.NUMPAD3 -> 96 + 3
            Key.NUMPAD4 -> 96 + 4
            Key.NUMPAD5 -> 96 + 5
            Key.NUMPAD6 -> 96 + 6
            Key.NUMPAD7 -> 96 + 7
            Key.NUMPAD8 -> 96 + 8
            Key.NUMPAD9 -> 96 + 9
            Key.MULTIPLY -> 106
            Key.ADD -> 107
            Key.SUBTRACT -> 109
            Key.DECIMAL_POINT -> 110
            Key.DIVIDE -> 111
            Key.F1 -> 112 + 0
            Key.F2 -> 112 + 1
            Key.F3 -> 112 + 2
            Key.F4 -> 112 + 3
            Key.F5 -> 112 + 4
            Key.F6 -> 112 + 5
            Key.F7 -> 112 + 6
            Key.F8 -> 112 + 7
            Key.F9 -> 112 + 8
            Key.F10 -> 112 + 9
            Key.F11 -> 112 + 10
            Key.F12 -> 112 + 11
            Key.NUM_LOCK -> 144
            Key.SCROLL_LOCK -> 145
            Key.MY_COMPUTER -> UNKNOWN_KEY
            Key.MY_CALCULATOR -> UNKNOWN_KEY
            Key.SEMI_COLON -> 186
            Key.EQUAL_SIGN -> 187
            Key.COMMA -> 188
            Key.DASH -> 189
            Key.PERIOD -> 190
            Key.FORWARD_SLASH -> 191
            Key.OPEN_BRACKET -> 219
            Key.BACK_SLASH -> 220
            Key.CLOSE_BRAKET -> 221
            Key.SINGLE_QUOTE -> 222
        }
    }
