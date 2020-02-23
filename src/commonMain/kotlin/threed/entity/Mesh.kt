package threed.entity

import threed.math.Vector3

class Mesh(
    val name: String = "unknown",
    val position: Vector3 = Vector3(),
    val rotation: Vector3 = Vector3(),
    val vertices: Array<Vertice> = emptyArray(),
    val verticesOrder: ShortArray
) : CanMove by Movable()
