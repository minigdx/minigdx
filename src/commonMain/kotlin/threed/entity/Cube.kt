package threed.entity

import threed.math.Vector3

data class Cube(
    val mesh: Mesh
) {

    constructor(name: String) : this(
        Mesh(
            name = name,
            vertices = arrayOf(
                Vector3(-1, 1, 1),
                Vector3(1, 1, 1),
                Vector3(-1, -1, 1),
                Vector3(-1, -1, -1),
                Vector3(-1, 1, -1),
                Vector3(1, 1, -1),
                Vector3(1, -1, 1),
                Vector3(1, -1, -1)
            )
        )
    )
}
