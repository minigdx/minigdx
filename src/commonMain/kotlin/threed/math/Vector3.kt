package threed.math

data class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {

    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())

}
