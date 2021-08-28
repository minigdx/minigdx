package com.github.dwursteisen.minigdx.ecs.entities

import com.github.dwursteisen.minigdx.Milliseconds
import kotlin.math.max
import kotlin.math.min

class AnimationBuilder {

    val animations: MutableMap<String, Map<Int, Milliseconds>> = mutableMapOf()

    fun addAnimation(name: String, startFrame: Int, endFrame: Int, frameDuration: Milliseconds) {
        addAnimation(name, startFrame, endFrame) { _, _ -> frameDuration }
    }

    fun addAnimation(
        name: String,
        startFrame: Int,
        endFrame: Int,
        frameDurationDescription: (frame: Int, frameRelative: Int) -> Milliseconds
    ) {
        val start = min(startFrame, endFrame)
        val end = max(startFrame, endFrame)

        val framesDescription = (start..end).map { frame ->
            val frameRelative = frame - start
            val duration = frameDurationDescription(frame, frameRelative)
            frame to duration
        }.toMap()

        animations[name] = framesDescription
    }
}
