package threed.buffer

sealed class DataSource {

    class FloatDataSource(val floats: FloatArray) : DataSource()
    class IntDataSource(val ints: IntArray) : DataSource()
    class DoubleDataSource(val double: DoubleArray) : DataSource()
}
