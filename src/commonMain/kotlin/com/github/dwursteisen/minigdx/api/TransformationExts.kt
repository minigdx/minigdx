package com.github.dwursteisen.minigdx.api

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.common.Transformation

fun Transformation.toMat4(): Mat4 {
    return this.t * this.r * this.s
}

internal val Transformation.t: Mat4
    get() {
        return Mat4.fromColumnMajor(*this.translation)
    }

internal val Transformation.r: Mat4
    get() {
        return Mat4.fromColumnMajor(*this.rotation)
    }

internal val Transformation.s: Mat4
    get() {
        return Mat4.fromColumnMajor(*this.scale)
    }
