package threed.math

data class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {

    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())

    fun add(x: Number, y: Number, z: Number): Vector3 {
        this.x += x.toFloat()
        this.y += y.toFloat()
        this.z += z.toFloat()
        return this
    }

    fun add(other: Vector3) = add(other.x, other.y, other.z)
}
