package threed.entity

import threed.math.Vector3

data class Mesh(
    val name: String = "unknown",
    val position: Vector3 = Vector3(),
    val rotation: Vector3 = Vector3(),
    val vertices: Array<Vector3> = emptyArray()
) {

    constructor(
        name: String,
        verticesCount: Int
    ) : this(
        name = name, vertices = Array<Vector3>(verticesCount) { Vector3() }
    )
}
