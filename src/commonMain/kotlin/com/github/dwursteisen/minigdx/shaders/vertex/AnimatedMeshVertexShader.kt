package com.github.dwursteisen.minigdx.shaders.vertex

import com.github.dwursteisen.minigdx.shaders.ShaderParameter
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AttributeVec2
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AttributeVec3
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AttributeVec4
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformArrayMat4
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformMat4

//language=GLSL
private fun shader(maxJoints: Int): String =
    """
        #ifdef GL_ES
        precision highp float;
        #endif

        const int MAX_JOINTS = $maxJoints;
        const int MAX_WEIGHTS = 4;
        
        uniform mat4 uModelView;
        // Light information
        uniform vec3 uLightPosition;
        uniform vec4 uLightColor;
        uniform float uLightIntensity;
        
        uniform mat4 uJointTransformationMatrix[MAX_JOINTS];
        
        attribute vec3 aVertexPosition;
        attribute vec3 aVertexNormal;

        attribute vec4 aJoints;
        attribute vec4 aWeights;
        attribute vec2 aUVPosition;
        
        varying vec2 vUVPosition;
        varying vec4 vLighting;
        
        void main() {
            vec4 totalLocalPos = vec4(0.0);
            vec4 totalNormalPos = vec4(0.0);
            
            for(int i=0;i<MAX_WEIGHTS;i++){
                int joinId = int(aJoints[i]);
                mat4 uJointMatrix = uJointTransformationMatrix[joinId];
                vec4 posePosition = uJointMatrix * vec4(aVertexPosition, 1.0);
                vec4 poseNormal = uJointMatrix * vec4(aVertexNormal, 1.0);
                
                // For an unknown reason, no evaluating aWeights make the 
                // computation not working on some devise (ie: reproduced on an Android TV).
                // This check is useless but make the things works!
                if(aWeights[i] >= 0.0) {
                    totalLocalPos += posePosition * aWeights[i];
                    totalNormalPos += poseNormal * aWeights[i];
                }
            }
        
            // Light computation
            vec3 n = normalize(aVertexNormal);
            vec3 lightDir = normalize(uLightPosition - totalLocalPos.xyz);
        	float diff = max(0.0, dot(n, lightDir));
         
            vec3 diffuse = diff * vec3(uLightColor);
            
            float distance = length(uLightPosition - totalLocalPos.xyz);
            vec3 radiance = vec3(uLightColor) * uLightIntensity;
            float attenuation = uLightIntensity / (distance * distance);
            radiance = radiance * attenuation;

            gl_Position = uModelView * totalLocalPos;
            
            vUVPosition = aUVPosition;
            vLighting = vec4(diffuse + attenuation * vec3(uLightColor), uLightColor.a);
        }
"""

class AnimatedMeshVertexShader(maxJoints: Int) : VertexShader(shader(maxJoints)) {

    val uModelView = UniformMat4("uModelView")
    val uJointTransformationMatrix = UniformArrayMat4("uJointTransformationMatrix")
    val uLightPosition = ShaderParameter.UniformVec3("uLightPosition")
    val uLightColor = ShaderParameter.UniformVec4("uLightColor")

    val aVertexPosition = AttributeVec3("aVertexPosition")
    val aVertexNormal = AttributeVec3("aVertexNormal")
    val aJoints = AttributeVec4("aJoints")
    val aWeights = AttributeVec4("aWeights")
    val aUVPosition = AttributeVec2("aUVPosition")
    val uLightIntensity = ShaderParameter.UniformFloat("uLightIntensity")

    override val parameters: List<ShaderParameter> = listOf(
        uModelView,
        uJointTransformationMatrix,
        uLightColor,
        uLightPosition,
        aVertexPosition,
        aVertexNormal,
        aUVPosition,
        aJoints,
        aWeights,
        uLightIntensity
    )
}
