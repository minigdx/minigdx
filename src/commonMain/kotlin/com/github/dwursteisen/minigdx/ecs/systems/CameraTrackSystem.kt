package com.github.dwursteisen.minigdx.ecs.systems

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.lookAt
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Camera
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class CameraTrackSystem : System(EntityQuery(Camera::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        entity.get(Camera::class).lookAt?.let { target ->
            val targetPosition = target.get(Position::class).translation
            val cameraPosition = entity.get(Position::class).translation

            val rotation = lookAt(cameraPosition.toFloat3(), targetPosition.toFloat3(), UP)
            entity.get(Position::class).setGlobalRotation(Quaternion.from(rotation))
        }
    }

    companion object {

        private val UP = Float3(0f, -1f, 0f)
    }
}
