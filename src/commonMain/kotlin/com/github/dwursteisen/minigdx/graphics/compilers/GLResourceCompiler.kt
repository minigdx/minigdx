package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.shaders.TextureReference

interface GLResourceCompiler {

    fun compile(gl: GL, component: GLResourceComponent, materials: MutableMap<Id, TextureReference>)

    /**
     * Update the target component using the source component data.
     *
     * It will synchronize the target with the source
     * which may be different instance but using the same id.
     */
    fun update(gl: GL, source: GLResourceComponent, target: GLResourceComponent)
}
