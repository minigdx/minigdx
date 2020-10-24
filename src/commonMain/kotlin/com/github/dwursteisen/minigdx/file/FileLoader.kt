package com.github.dwursteisen.minigdx.file

interface FileLoader<T> {

    fun load(filename: String, handler: FileHandler): Content<T>
}
