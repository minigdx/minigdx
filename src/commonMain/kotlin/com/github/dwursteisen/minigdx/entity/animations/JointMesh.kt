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
import com.github.dwursteisen.minigdx.graphics.Render
import com.github.dwursteisen.minigdx.math.Vector3

class JointMesh(
    val mesh: Mesh,
    currentPose: Armature
) : CanDraw by Drawable(Render(mesh, currentPose)),
    CanMove by Movable() {

    companion object {

        fun of(joint: Joint, currentPose: Armature): JointMesh {
            val mat = joint.globalInverseBindTransformation
            return JointMesh(
                Mesh(
                    drawType = DrawType.TRIANGLE,
                    modelMatrix = mat,
                    vertices = arrayOf(
                        Vertice(
                            position = Vector3(0f, 0f, 0f),
                            normal = Vector3(1, 1, 1),
                            color = Colors.RED,
                            influence = Influence(
                                joinIds = JointsIndex(joint.id)
                            )
                        ),
                        Vertice(
                            position = Vector3(-0.25f, 0.25f, 0f),
                            normal = Vector3(1, 1, 1),
                            color = Colors.WHITE,
                            influence = Influence(
                                joinIds = JointsIndex(joint.id)
                            )
                        ),
                        Vertice(
                            position = Vector3(0.25f, 0.25f, 0f),
                            normal = Vector3(1, 1, 1),
                            color = Colors.WHITE,
                            influence = Influence(
                                joinIds = JointsIndex(joint.id)
                            )
                        ),
                        Vertice(
                            position = Vector3(0f, 1f, 0f),
                            normal = Vector3(1, 1, 1),
                            color = Colors.BLUE,
                            influence = Influence(
                                joinIds = JointsIndex(joint.id)
                            )
                        )
                    ),
                    verticesOrder = shortArrayOf(
                        0, 1, 2,
                        1, 2, 3
                    )
                ), currentPose
            )
        }
    }
}
