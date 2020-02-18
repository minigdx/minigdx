package threed.shaders

object DefaultShaders {

    val vertexShader = """
        #ifdef GL_ES
        precision mediump float;
        #endif

        uniform mat4 uModelViewMatrix;
        uniform mat4 uProjectionMatrix;
        
        attribute vec4 aVertexPosition;
        attribute vec4 aVertexColor;
        
        varying vec4 vColor;
        
        void main() {
            gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;
            
            vColor = aVertexColor;
        }
    """.trimIndent()

    val fragmentShader = """
        #ifdef GL_ES
        precision mediump float;
        #endif

        varying vec4 vColor;
        
        void main() {
              // see vertex shader
              gl_FragColor = vColor;
        }
    """.trimIndent()
}
