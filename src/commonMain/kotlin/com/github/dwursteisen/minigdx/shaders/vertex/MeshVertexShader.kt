package com.github.dwursteisen.minigdx.shaders.vertex

import com.github.dwursteisen.minigdx.shaders.ShaderParameter
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AttributeVec2
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AttributeVec3
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformFloat
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformMat4
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformVec3
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformVec4

//language=GLSL
private val simpleVertexShader =
    """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        uniform mat4 uModelView;
        // Light information
        uniform vec3 uLightPosition;
        uniform vec4 uLightColor;
        uniform float uLightIntensity;
        
        attribute vec3 aVertexPosition;
        attribute vec3 aVertexNormal;
        attribute vec2 aUVPosition;
        
        varying vec2 vUVPosition;
        varying vec4 vLighting;
        
        void main() {
            // Light computation
            vec3 n = normalize(aVertexNormal);
            vec3 lightDir = normalize(uLightPosition - aVertexPosition);
        	float diff = max(0.0, dot(n, lightDir));
         
            vec3 diffuse = diff * vec3(uLightColor);
            
            float distance = length(uLightPosition - aVertexPosition);
            vec3 radiance = vec3(uLightColor) * uLightIntensity;
            float attenuation = uLightIntensity / (distance * distance);
            radiance = radiance * attenuation;
            
            gl_Position = uModelView * vec4(aVertexPosition, 1.0);
            vUVPosition = aUVPosition;
            
            vLighting = vec4(diffuse + attenuation * vec3(uLightColor), uLightColor.a);
        }
    """.trimIndent()

class MeshVertexShader : VertexShader(
    shader = simpleVertexShader
) {
    val uModelView = UniformMat4("uModelView")
    val aVertexPosition = AttributeVec3("aVertexPosition")
    val aVertexNormal = AttributeVec3("aVertexNormal")
    val aUVPosition = AttributeVec2("aUVPosition")

    val uLightPosition = UniformVec3("uLightPosition")
    val uLightColor = UniformVec4("uLightColor")
    val uLightIntensity = UniformFloat("uLightIntensity")

    override val parameters: List<ShaderParameter> = listOf(
        uModelView,
        aVertexPosition,
        aVertexNormal,
        aUVPosition,
        uLightPosition,
        uLightColor,
        uLightIntensity
    )
}
