package threed.math

import kotlin.math.tan


private operator fun FloatArray.set(x: Int, y: Int, value: Number) {
    this[indice(x, y)] = value.toFloat()
}


private operator fun FloatArray.get(x: Int, y: Int): Float {
    return this[indice(x, y)]
}

private fun indice(x: Int, y: Int) = x * 4 + y

class Matrix4(val data: FloatArray) {

    constructor(vararg floats: Float) : this(data = floats)

    fun setTranslate(transform: Vector3): Matrix4 {
        data[3, 0] = transform.x
        data[3, 1] = transform.y
        data[3, 2] = transform.z
        return this
    }

    fun translate(transform: Vector3): Matrix4 {
        data[3, 0] = data[0, 0] * transform.x + data[1, 0] * transform.y + data[2, 0] * transform.z + data[3, 0]
        data[3, 1] = data[0, 1] * transform.x + data[1, 1] * transform.y + data[2, 1] * transform.z + data[3, 1]
        data[3, 2] = data[0, 2] * transform.x + data[1, 2] * transform.y + data[2, 2] * transform.z + data[3, 2]
        data[3, 3] = data[0, 3] * transform.x + data[1, 3] * transform.y + data[2, 3] * transform.z + data[3, 3]
        return this
    }

    override fun toString(): String {
        return """
            ${data[0, 0]},${data[1, 0]},${data[2, 0]},${data[3, 0]},
            ${data[0, 1]},${data[1, 1]},${data[2, 1]},${data[3, 1]},
            ${data[0, 2]},${data[1, 2]},${data[2, 2]},${data[3, 2]},
            ${data[0, 3]},${data[1, 3]},${data[2, 3]},${data[3, 3]},
        """.trimIndent()

    }

    companion object {
        fun zero(): Matrix4 {
            val data = FloatArray(4 * 4) { 0f }
            return Matrix4(data)
        }

        fun identity(): Matrix4 {
            val idt = zero()
            idt.data[0, 0] = 1
            idt.data[1, 1] = 1
            idt.data[2, 2] = 1
            idt.data[3, 3] = 1
            return idt
        }

        fun projection(fov: Number, aspect: Number, near: Number, far: Number): Matrix4 {
            val idt = identity()
            val scale = 1f / tan(fov.toFloat() * 0.5f)
            idt.data[0, 0] = scale / aspect.toFloat()
            idt.data[1, 1] = scale
            val f = far.toFloat()
            val n = near.toFloat()
            idt.data[2, 2] = -f / (f - n)
            idt.data[3, 2] = -f * n / (f - n)
            idt.data[2, 3] = -1
            idt.data[3, 3] = 0
            return idt

        }
    }
}
