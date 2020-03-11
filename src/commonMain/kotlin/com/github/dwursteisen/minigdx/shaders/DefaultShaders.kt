package com.github.dwursteisen.minigdx.shaders

object DefaultShaders {

    val MAX_JOINTS = 20

    //language=GLSL
    val vertexShader = """
        #ifdef GL_ES
        precision highp float;
        #endif

        const int MAX_JOINTS = $MAX_JOINTS;
        
        uniform mat4 uModelMatrix;
        uniform mat4 uViewMatrix;
        uniform mat4 uProjectionMatrix;
        uniform mat4 uNormalMatrix;
        uniform int uArmature;
        
        uniform mat4 uJointTransformationMatrix[MAX_JOINTS];
        
        attribute vec3 aVertexPosition;
        attribute vec3 aNormal;
        attribute vec4 aVertexColor;
        attribute vec3 aJoints;
        
        varying vec4 vColor;
        varying vec3 vLighting;
        
        void main() {
            mat4 uJointMatrix;
            if(uArmature > 0) {
                uJointMatrix = uJointTransformationMatrix[int(aJoints.x)]; 
            } else {
               uJointMatrix = mat4(1.0);
            }
            gl_Position = uProjectionMatrix * uViewMatrix * uModelMatrix * uJointMatrix * vec4(aVertexPosition, 1.0);
            
            // Apply lighting effect
                
            vec3 ambientLight = vec3(0.6, 0.6, 0.6);
            vec3 directionalLightColor = vec3(0.5, 0.5, 0.75);
            vec3 directionalVector = vec3(0.85, 0.8, 0.75);
            
            vec4 transformedNormal = uNormalMatrix * vec4(aNormal, 1.0);
            
            float directional = max(dot(transformedNormal.xyz, directionalVector), 0.0);
            vLighting = ambientLight + (directionalLightColor * directional);
            // vLighting = vec3(0.5,0.5,0.5);
            vColor = aVertexColor;
        }
    """.trimIndent()

    val fragmentShader = """
        #ifdef GL_ES
        precision highp float;
        #endif

        varying vec4 vColor;
        varying vec3 vLighting;
        
        void main() {
              // see vertex shader
              // gl_FragColor = vColor;
              gl_FragColor = vec4(vColor.rgb * vLighting.rgb, vColor.a);
        }
    """.trimIndent()
}
