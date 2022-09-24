package com.github.dwursteisen.minigdx.math

/**
 * Vector in which you can only <strong>read</strong> values.
 * Those values can be updated by another entity, that's why
 * you can only read as you might loose your modification otherwise.
 */
class ImmutableVector2(internal val delegate: Vector2) {

    val x: Float
        get() = delegate.x
    val y: Float
        get() = delegate.y

    val width: Float
        get() = delegate.x
    val height: Float
        get() = delegate.y

    fun mutable(): Vector2 {
        return delegate.copy()
    }
}
