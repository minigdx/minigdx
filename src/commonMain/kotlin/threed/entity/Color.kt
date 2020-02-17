package threed.entity

import threed.Percent

data class Color(var r: Float, var g: Float, var b: Float, var alpha: Float) {

    constructor(r: Percent, g: Percent, b: Percent, alpha: Percent) : this(
        r.toFloat(),
        g.toFloat(),
        b.toFloat(),
        alpha.toFloat()
    )
}
