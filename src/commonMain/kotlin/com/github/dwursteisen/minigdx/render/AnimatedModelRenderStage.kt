package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.inverse
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.AnimatedComponent
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.components.LightComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.AnimatedMeshVertexShader

class AnimatedModelRenderStage(gameContext: GameContext) :
    RenderStage<AnimatedMeshVertexShader, UVFragmentShader>(
        gameContext = gameContext,
        vertex = AnimatedMeshVertexShader(gameContext.options.jointLimit),
        fragment = UVFragmentShader(),
        query = EntityQuery(AnimatedComponent::class)
    ) {

    override fun update(delta: Seconds, entity: Entity) {
        val model = entity.get(Position::class).transformation
        val animatedModel = entity.get(AnimatedComponent::class)

        vertex.uModelView.apply(program, combinedMatrix * model)
        vertex.uJointTransformationMatrix.apply(program, animatedModel.currentPose)

        // Configure the light.
        vertex.uLightNumber.apply(program, lights.size)

        val lightIntensities = mutableListOf<Float>()
        val lightColors = mutableListOf<Color>()
        val lightPositions = mutableListOf<Float>()
        lights.forEach { currentLight ->

            val intensity = currentLight.get(LightComponent::class).intensity.toFloat() / 1000f
            lightIntensities.add(intensity)

            // We configure the current light
            // Set the light in the projection space
            val translation = (inverse(model) * currentLight.get(Position::class).transformation).translation
            lightPositions.addAll(listOf(translation.x, translation.y, translation.z))

            val color = currentLight.get(LightComponent::class).color
            lightColors.add(color)
        }

        vertex.uLightColor.apply(program, lightColors)
        vertex.uLightIntensity.apply(program, lightIntensities)
        vertex.uLightPosition.apply(program, lightPositions)

        animatedModel.animatedModel.models.flatMap { m -> m.primitives }.forEach { primitive ->
            vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
            vertex.aVertexNormal.apply(program, primitive.normalsBuffer!!)
            vertex.aUVPosition.apply(program, primitive.uvsBuffer!!)
            vertex.aWeights.apply(program, primitive.weightsBuffer!!)
            vertex.aJoints.apply(program, primitive.jointsBuffer!!)
            fragment.uUV.apply(program, primitive.texture.textureReference!!, unit = 0)

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
            gl.drawElements(
                GL.TRIANGLES,
                primitive.verticesOrder.size,
                GL.UNSIGNED_SHORT,
                0
            )
        }
    }
}
