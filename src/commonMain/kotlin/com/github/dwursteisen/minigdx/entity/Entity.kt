package com.github.dwursteisen.minigdx.entity

import com.github.dwursteisen.minigdx.Seconds

interface Entity {

    fun update(delta: Seconds) = Unit
}
