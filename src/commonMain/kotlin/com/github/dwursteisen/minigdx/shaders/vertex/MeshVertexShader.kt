package com.github.dwursteisen.minigdx.shaders.vertex

import com.github.dwursteisen.minigdx.shaders.ShaderParameter
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AttributeVec2
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AttributeVec3
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformArrayFloat
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformArrayVec3
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformArrayVec4
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformMat4

//language=GLSL
private val simpleVertexShader =
    """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        const int MAX_LIGHTS = 5;
        
        uniform mat4 uModelView;
        // Light information
        uniform vec3 uLightPosition[MAX_LIGHTS];
        uniform vec4 uLightColor[MAX_LIGHTS];
        uniform float uLightIntensity[MAX_LIGHTS];
        uniform int uLightNumber;
        
        attribute vec3 aVertexPosition;
        attribute vec3 aVertexNormal;
        attribute vec2 aUVPosition;
        
        varying vec2 vUVPosition;
        varying vec4 vLighting;
        
        void main() {
            vec4 lightColor = vec4(0.0);
            vec3 n = normalize(aVertexNormal);
        
            for (int i = 0; i < MAX_LIGHTS; i++) {
                if(i >= uLightNumber) { break; }
                // Light computation
                vec3 lightDir = normalize(uLightPosition[i] - aVertexPosition);
                float diff = max(0.0, dot(n, lightDir));
             
                vec3 diffuse = diff * vec3(uLightColor[i]);
                
                float distance = length(uLightPosition[i] - aVertexPosition);
                vec3 radiance = vec3(uLightColor[i]) * uLightIntensity[i];
                float attenuation = uLightIntensity[i] / (distance * distance);
                radiance = radiance * attenuation;
                lightColor += vec4(diffuse + attenuation * vec3(uLightColor[i]), uLightColor[i].a);
            }
            
            gl_Position = uModelView * vec4(aVertexPosition, 1.0);
            vUVPosition = aUVPosition;
            vLighting = lightColor;
            
        }
    """.trimIndent()

class MeshVertexShader : VertexShader(
    shader = simpleVertexShader
) {
    val uModelView = UniformMat4("uModelView")
    val aVertexPosition = AttributeVec3("aVertexPosition")
    val aVertexNormal = AttributeVec3("aVertexNormal")
    val aUVPosition = AttributeVec2("aUVPosition")

    val uLightPosition = UniformArrayVec3("uLightPosition")
    val uLightColor = UniformArrayVec4("uLightColor")
    val uLightIntensity = UniformArrayFloat("uLightIntensity")
    val uLightNumber = ShaderParameter.UniformInt("uLightNumber")

    override val parameters: List<ShaderParameter> = listOf(
        uModelView,
        aVertexPosition,
        aVertexNormal,
        aUVPosition,
        uLightPosition,
        uLightColor,
        uLightIntensity,
        uLightNumber,
    )
}
