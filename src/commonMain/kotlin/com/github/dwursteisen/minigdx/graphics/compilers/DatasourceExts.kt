package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.model.Color
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.UV
import com.github.dwursteisen.minigdx.shaders.DataSource

fun List<Position>.positionsDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(
        FloatArray(this.size * 3) { index ->
            val y = index % 3
            val x = (index - y) / 3
            when (y) {
                0 -> this[x].x
                1 -> this[x].y
                2 -> this[x].z
                else -> throw IllegalArgumentException("index '$index' not expected.")
            }
        }
    )
}

fun List<Normal>.normalsDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(
        FloatArray(this.size * 3) { index ->
            val y = index % 3
            val x = (index - y) / 3
            when (y) {
                0 -> this[x].x
                1 -> this[x].y
                2 -> this[x].z
                else -> throw IllegalArgumentException("index '$index' not expected.")
            }
        }
    )
}

fun List<Color>.colorsDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(
        FloatArray(this.size * 4) { index ->
            val y = index % 4
            val x = (index - y) / 4
            when (y) {
                0 -> this[x].r
                1 -> this[x].g
                2 -> this[x].b
                3 -> this[x].alpha
                else -> throw IllegalArgumentException("index '$index' not expected.")
            }
        }
    )
}

fun List<UV>.uvDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(
        FloatArray(this.size * 2) { index ->
            val y = index % 2
            val x = (index - y) / 2
            when (y) {
                0 -> this[x].x
                1 -> this[x].y
                else -> throw IllegalArgumentException("index '$index' not expected.")
            }
        }
    )
}

fun List<Float>.weightDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(this.toFloatArray())
}

fun List<Int>.jointDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(this.map { it.toFloat() }.toFloatArray())
}
