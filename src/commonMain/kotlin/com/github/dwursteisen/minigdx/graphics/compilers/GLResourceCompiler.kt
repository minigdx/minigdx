package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.shaders.TextureReference

interface GLResourceCompiler {

    fun compile(gl: GL, component: GLResourceComponent, materials: MutableMap<Id, TextureReference>)

    fun update(gl: GL, source: GLResourceComponent, target: GLResourceComponent)
}
