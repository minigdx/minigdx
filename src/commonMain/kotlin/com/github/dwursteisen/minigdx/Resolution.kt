package com.github.dwursteisen.minigdx

data class Resolution(val width: Pixel, val height: Pixel) {

    val ratio: Ratio = width / height.toFloat()
}
