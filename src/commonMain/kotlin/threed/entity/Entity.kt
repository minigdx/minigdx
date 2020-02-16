package threed.entity

import threed.math.Vector3

interface Entity {

    fun translate(x: Number, y: Number, z: Number): Entity
    fun transpose(transpose: Vector3): Entity = translate(transpose.x, transpose.y, transpose.z)
}
