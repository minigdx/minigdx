package threed.shaders

object DefaultShaders {

    val vertexShader = """
        #ifdef GL_ES
        precision highp float;
        #endif

        uniform mat4 uModelViewMatrix;
        uniform mat4 uProjectionMatrix;
        uniform mat4 uNormalMatrix;
        
        attribute vec3 aVertexPosition;
        attribute vec3 aNormal;
        attribute vec4 aVertexColor;
        
        varying vec4 vColor;
        varying vec3 vLighting;
        
        void main() {
            gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(aVertexPosition, 1.0);
            
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
