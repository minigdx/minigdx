package com.github.dwursteisen.minigdx.buffer

sealed class DataSource {

    class FloatDataSource(val floats: FloatArray) : DataSource()
    class IntDataSource(val ints: IntArray) : DataSource()
    class ShortDataSource(val shorts: ShortArray) : DataSource()
    class UIntDataSource(val ints: IntArray) : DataSource()
    class DoubleDataSource(val double: DoubleArray) : DataSource()
}
