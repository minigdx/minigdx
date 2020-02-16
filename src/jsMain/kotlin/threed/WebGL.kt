package threed

import org.khronos.webgl.WebGLRenderingContext

class WebGL(private val context: WebGLRenderingContext) : GL {

    override fun clearColor(r: Percent, g: Percent, b: Percent, a: Percent) {
        context.clearColor(r.toPercent(), g.toPercent(), b.toPercent(), a.toPercent())
    }

    override fun clear(mask: ByteMask) {
        context.clear(mask)
    }
}
