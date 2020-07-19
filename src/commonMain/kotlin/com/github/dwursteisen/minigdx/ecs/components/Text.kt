package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.entity.text.Font

class Text(
    var text: String,
    val font: Font
) : Component {

    var previousText: String = ""
}
