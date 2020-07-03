package com.github.dwursteisen.minigdx.shaders

//language=GLSL
private const val shader: String = """
        #ifdef GL_ES
        precision highp float;
        #endif

        const int MAX_JOINTS = 40;
        const int MAX_WEIGHTS = 4;
        
        uniform mat4 uModelView;
        
        uniform mat4 uJointTransformationMatrix[MAX_JOINTS];
        
        attribute vec3 aVertexPosition;
        attribute vec4 aJoints;
        attribute vec4 aWeights;
        attribute vec2 aUVPosition;
        
        varying vec2 vUVPosition;
        
        void main() {
            vec4 totalLocalPos = vec4(0.0);
            
            for(int i=0;i<MAX_WEIGHTS;i++){
                int joinId = int(aJoints[i]);
                mat4 uJointMatrix = uJointTransformationMatrix[joinId];
                vec4 posePosition = uJointMatrix * vec4(aVertexPosition, 1.0);
                totalLocalPos += posePosition * aWeights[i];
            }
        
            gl_Position = uModelView * totalLocalPos;
            
            vUVPosition = aUVPosition;
        }
"""

class AnimatedMeshVertexShader : VertexShader(shader) {

    val uModelView =
        ShaderParameter.UniformMat4("uModelView")
    val uJointTransformationMatrix =
        ShaderParameter.UniformArrayMat4("uJointTransformationMatrix")

    val aVertexPosition =
        ShaderParameter.AttributeVec3("aVertexPosition")
    val aJoints =
        ShaderParameter.AttributeVec4("aJoints")
    val aWeights =
        ShaderParameter.AttributeVec4("aWeights")
    val aUVPosition =
        ShaderParameter.AttributeVec2("aUVPosition")

    override val parameters: List<ShaderParameter> = listOf(
        uModelView,
        uJointTransformationMatrix,
        aVertexPosition,
        aUVPosition,
        aJoints,
        aWeights
    )
}
