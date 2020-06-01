package com.github.dwursteisen.minigdx.shaders

import kotlin.test.Test
import kotlin.test.assertEquals

class ShaderDslTest {

    @Test
    fun createAttribute() {
        val vertex = vertex {
            val exampleOfName = attribute { vec4("aAttribute") }
        }

        val shader = vertex.toString()
        assertEqualsIgnoreEmptyLines("""
            #ifdef GL_ES
                precision highp float;
            #endif
        
        
            attribute vec4 aAttribute;
            
            void main() {
            
            }
        """.trimIndent(), shader)
    }

    @Test
    fun createUniform() {
        val vertex = vertex {
            val exampleOfName = uniform { mat4("aUniform") }
        }

        val shader = vertex.toString()
        assertEqualsIgnoreEmptyLines("""
            #ifdef GL_ES
                precision highp float;
            #endif
        
            uniform mat4 aUniform;
            
            
            void main() {
            
            }
        """.trimIndent(), shader)
    }

    @Test
    fun setGlPosition() {
        val vertex = vertex {
            val a = uniform { vec3("a") }
            val b = uniform { vec3("b") }
            val c = uniform { vec3("c") }

            glPosition { a + b + c }
        }

        val shader = vertex.toString()
        assertEqualsIgnoreEmptyLines("""
            #ifdef GL_ES
                precision highp float;
            #endif
        
            uniform vec3 a;
            uniform vec3 b;
            uniform vec3 c;
            
            
            void main() {
                gl_Position = a + b + c;
            }
        """.trimIndent(), shader)
    }

    private fun assertEqualsIgnoreEmptyLines(expected: String, actual: String) {
        val a = expected.split("\n").filter {
            it.isNotBlank()
        }.joinToString("\n")

        val b = actual.split("\n").filter {
            it.isNotBlank()
        }.joinToString("\n")

        assertEquals(a, b)
    }
}
