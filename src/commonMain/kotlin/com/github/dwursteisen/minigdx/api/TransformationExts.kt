package com.github.dwursteisen.minigdx.api

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.common.Transformation

fun Transformation.toMat4(): Mat4 {
    return Mat4.fromColumnMajor(*this.matrix)
}
