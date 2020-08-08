import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.Vertex

object ModelFactory {

    fun vertex(x: Number, y: Number, z: Number, nx: Number = 1f, ny: Number = 1f, nz: Number = 1f): Vertex {
        return Vertex(
            position = Position(x.toFloat(), y.toFloat(), z.toFloat()),
            normal = Normal(nx.toFloat(), ny.toFloat(), nz.toFloat())
        )
    }
}
