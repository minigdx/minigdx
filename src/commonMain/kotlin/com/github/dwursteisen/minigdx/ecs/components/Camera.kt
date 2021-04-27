package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.ortho
import com.curiouscreature.kotlin.math.perspective
import com.github.dwursteisen.minigdx.GameScreen
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class Camera(
    private val gameScreen: GameScreen,
    type: Type,

    far: Float,
    near: Float = 0f,

    fov: Float = 0f,
    scale: Float = 0f,

    var lookAt: Entity? = null,
) : Component {

    private var needsToBeUpdated = true
    private var _projection: Mat4 = Mat4.identity()

    var type: Type = type
        set(value) {
            needsToBeUpdated = true
            field = value
        }

    var far: Float = far
        set(value) {
            needsToBeUpdated = true
            field = value
        }

    var near: Float = near
        set(value) {
            needsToBeUpdated = true
            field = value
        }

    var fov: Float = fov
        set(value) {
            needsToBeUpdated = true
            field = value
        }

    var scale: Float = scale
        set(value) {
            needsToBeUpdated = true
            field = value
        }

    val projection: Mat4
        get() {
            if (needsToBeUpdated) {
                needsToBeUpdated = false
                _projection = when (type) {
                    Type.PERSPECTIVE -> perspective(
                        fov = fov,
                        aspect = gameScreen.ratio,
                        near = near,
                        far = far
                    )
                    Type.ORTHOGRAPHIC -> {
                        val (w, h) = if (gameScreen.width >= gameScreen.height) {
                            // 1 / GameScreen.ratio
                            1f to (gameScreen.height / gameScreen.width.toFloat())
                        } else {
                            // GameScreen.ratio
                            gameScreen.width / gameScreen.height.toFloat() to 1f
                        }

                        ortho(
                            l = -scale * 0.5f * w,
                            r = scale * 0.5f * w,
                            b = -scale * 0.5f * h,
                            t = scale * 0.5f * h,
                            n = near,
                            f = far
                        )
                    }
                }
            }

            return _projection
        }

    enum class Type {
        PERSPECTIVE,
        ORTHOGRAPHIC
    }
}
