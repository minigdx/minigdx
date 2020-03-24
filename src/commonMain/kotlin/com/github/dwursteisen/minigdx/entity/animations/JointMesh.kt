package com.github.dwursteisen.minigdx.entity.animations

import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.Colors
import com.github.dwursteisen.minigdx.entity.DrawType
import com.github.dwursteisen.minigdx.entity.Influence
import com.github.dwursteisen.minigdx.entity.JointsIndex
import com.github.dwursteisen.minigdx.entity.Mesh
import com.github.dwursteisen.minigdx.entity.Vertice
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.math.Vector3

class JointMesh(
    val mesh: Mesh,
    currentPose: Armature
// FIXME: test without bone transformation
) : CanDraw by Drawable(mesh, null),
    CanMove by Movable() {

    companion object {

        private fun Joint.toVertice(): Vertice {
            // joint position = position + scale ?
            // bone = 1 unit * scale ?
            val position = globalBindTransformation.position

            return Vertice(
                position = Vector3(position.x, position.y, position.z),
                normal = Vector3(1, 1, 1),
                color = Colors.RED,
                influence = Influence(
                    joinIds = JointsIndex(id)
                )
            )
        }

        fun of(
            referencePose: Armature,
            currentPose: Armature
        ): JointMesh {
            val positions = Array(referencePose.allJoints.size) {
                referencePose[it].toVertice()
            }

            val order = mutableListOf<Short>()
            positions.forEachIndexed { index, vertex ->
                val parent = referencePose[index].parent
                if (parent != null) {
                    order.add(parent.id.toShort())
                    order.add(index.toShort())
                }
            }

            return JointMesh(
                Mesh(
                    drawType = DrawType.LINE,
                    vertices = positions,
                    verticesOrder = order.toShortArray()
                ), currentPose
            )
        }
    }
}
