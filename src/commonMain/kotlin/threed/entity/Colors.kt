package threed.entity

import kotlin.random.Random

object Colors {

    private val random = Random(0)

    fun random(alpha: Float = 1.0f): Color {
        return Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), alpha)
    }

    val WHITE = Color(1, 1, 1, 1)
    val BLACK = Color(0, 0, 0, 1)
}
