package threed.math

import kotlin.math.*


private operator fun FloatArray.set(x: Int, y: Int, value: Number) {
    this[indice(x, y)] = value.toFloat()
}


private operator fun FloatArray.get(x: Int, y: Int): Float {
    return this[indice(x, y)]
}

private fun indice(x: Int, y: Int) = x * 4 + y

const val RAD2DEG = 180f / PI.toFloat();
const val DEG2RAD = PI.toFloat() / 180f

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

    fun rotate(axes: Vector3, degrees: Float): Matrix4 {
        var (x, y, z) = axes
        var length: Float = sqrt(x * x + y * y + z * z)

        if (length != 1.0f && length != 0.0f) {
            length = 1.0f / length
            x *= length
            y *= length
            z *= length
        }

        val sinres: Float = sin(degrees * DEG2RAD)
        val cosres: Float = cos(degrees * DEG2RAD)
        val t = 1.0f - cosres

       /* tmp.data[0, 0] = x * x * t + cosres
        tmp.data[0, 1] = y * x * t + z * sinres
        tmp.data[0, 2] = z * x * t - y * sinres
        tmp.data[0, 3] = 0.0f

        tmp.data[1, 0] = x * y * t - z * sinres
        tmp.data[1, 1] = y * y * t + cosres
        tmp.data[1, 2] = z * y * t + x * sinres
        tmp.data[1, 3] = 0.0f

        tmp.data[2, 0] = x * z * t + y * sinres
        tmp.data[2, 1] = y * z * t - x * sinres
        tmp.data[2, 2] = z * z * t + cosres
        tmp.data[2, 3] = 0.0f

        tmp.data[3, 0] = 0.0f
        tmp.data[3, 1] = 0.0f
        tmp.data[3, 2] = 0.0f
        tmp.data[3, 3] = 1.0f*/
        tmp.data[0, 0] = x * x * t + cosres
        tmp.data[0, 1] = y * x * t + z * sinres
        tmp.data[0, 2] = z * x * t - y * sinres
        tmp.data[0, 3] = 0.0f
        tmp.data[1, 0] = x * y * t - z * sinres
        tmp.data[1, 1] = y * y * t + cosres
        tmp.data[1, 2] = z * y * t + x * sinres
        tmp.data[1, 3] = 0.0f
        tmp.data[2, 0] = x * z * t + y * sinres
        tmp.data[2, 1] = y * z * t - x * sinres
        tmp.data[2, 2] = z * z * t + cosres
        tmp.data[2, 3] = 0.0f
        tmp.data[3, 0] = 0.0f
        tmp.data[3, 1] = 0.0f
        tmp.data[3, 2] = 0.0f
        tmp.data[3, 3] = 1.0f
        // tmp.data.copyInto(this.data)
        //return this
        return multiply(tmp)
    }

    fun multiply(right: Matrix4): Matrix4 {
        val left = this.copy()
        this.data[0, 0] =
            left.data[0, 0] * right.data[0, 0] + left.data[1, 0] * right.data[0, 1] + left.data[2, 0] * right.data[0, 2] + left.data[3, 0] * right.data[0, 3];
        this.data[1, 0] =
            left.data[0, 0] * right.data[1, 0] + left.data[1, 0] * right.data[1, 1] + left.data[2, 0] * right.data[1, 2] + left.data[3, 0] * right.data[1, 3];
        this.data[2, 0] =
            left.data[0, 0] * right.data[2, 0] + left.data[1, 0] * right.data[2, 1] + left.data[2, 0] * right.data[2, 2] + left.data[3, 0] * right.data[2, 3];
        this.data[3, 0] =
            left.data[0, 0] * right.data[3, 0] + left.data[1, 0] * right.data[3, 1] + left.data[2, 0] * right.data[3, 2] + left.data[3, 0] * right.data[3, 3];
        this.data[0, 1] =
            left.data[0, 1] * right.data[0, 0] + left.data[1, 1] * right.data[0, 1] + left.data[2, 1] * right.data[0, 2] + left.data[3, 1] * right.data[0, 3];
        this.data[1, 1] =
            left.data[0, 1] * right.data[1, 0] + left.data[1, 1] * right.data[1, 1] + left.data[2, 1] * right.data[1, 2] + left.data[3, 1] * right.data[1, 3];
        this.data[2, 1] =
            left.data[0, 1] * right.data[2, 0] + left.data[1, 1] * right.data[2, 1] + left.data[2, 1] * right.data[2, 2] + left.data[3, 1] * right.data[2, 3];
        this.data[3, 1] =
            left.data[0, 1] * right.data[3, 0] + left.data[1, 1] * right.data[3, 1] + left.data[2, 1] * right.data[3, 2] + left.data[3, 1] * right.data[3, 3];
        this.data[0, 2] =
            left.data[0, 2] * right.data[0, 0] + left.data[1, 2] * right.data[0, 1] + left.data[2, 2] * right.data[0, 2] + left.data[3, 2] * right.data[0, 3];
        this.data[1, 2] =
            left.data[0, 2] * right.data[1, 0] + left.data[1, 2] * right.data[1, 1] + left.data[2, 2] * right.data[1, 2] + left.data[3, 2] * right.data[1, 3];
        this.data[2, 2] =
            left.data[0, 2] * right.data[2, 0] + left.data[1, 2] * right.data[2, 1] + left.data[2, 2] * right.data[2, 2] + left.data[3, 2] * right.data[2, 3];
        this.data[3, 2] =
            left.data[0, 2] * right.data[3, 0] + left.data[1, 2] * right.data[3, 1] + left.data[2, 2] * right.data[3, 2] + left.data[3, 2] * right.data[3, 3];
        this.data[0, 3] =
            left.data[0, 3] * right.data[0, 0] + left.data[1, 3] * right.data[0, 1] + left.data[2, 3] * right.data[0, 2] + left.data[3, 3] * right.data[0, 3];
        this.data[1, 3] =
            left.data[0, 3] * right.data[1, 0] + left.data[1, 3] * right.data[1, 1] + left.data[2, 3] * right.data[1, 2] + left.data[3, 3] * right.data[1, 3];
        this.data[2, 3] =
            left.data[0, 3] * right.data[2, 0] + left.data[1, 3] * right.data[2, 1] + left.data[2, 3] * right.data[2, 2] + left.data[3, 3] * right.data[2, 3];
        this.data[3, 3] =
            left.data[0, 3] * right.data[3, 0] + left.data[1, 3] * right.data[3, 1] + left.data[2, 3] * right.data[3, 2] + left.data[3, 3] * right.data[3, 3];
        return this
    }

    fun copy(): Matrix4 {
        return Matrix4(data = this.data.copyOf())
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
        private val tmp = zero()

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
