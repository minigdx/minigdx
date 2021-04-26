package com.github.dwursteisen.minigdx.file

import android.content.Context
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.logger.Logger
import de.matthiasmann.twl.utils.PNGDecoder
import java.nio.Buffer
import java.nio.ByteBuffer

actual class PlatformFileHandler(private val context: Context, actual val logger: Logger) {

    @ExperimentalStdlibApi
    actual fun read(filename: String): Content<String> {
        val data = context.assets.open(filename).readBytes().decodeToString()
        logger.info("FILE_HANDLER") { "Reading '$filename' as String content" }
        val content = Content<String>(filename, logger)
        content.load(data)
        return content
    }

    actual fun readData(filename: String): Content<ByteArray> {
        val data = context.assets.open(filename).readBytes()
        logger.info("FILE_HANDLER") { "Reading '$filename' as Byte content" }
        val content = Content<ByteArray>(filename, logger)
        content.load(data)
        return content
    }

    actual fun readTextureImage(filename: String): Content<TextureImage> {
        val content = Content<TextureImage>(filename, logger)
        val inputStream = context.assets.open(filename)

        val decoder = PNGDecoder(inputStream)

        // create a byte buffer big enough to store RGBA values
        val buffer =
            ByteBuffer.allocateDirect(4 * decoder.width * decoder.height)

        // decode
        decoder.decode(buffer, decoder.width * 4, PNGDecoder.Format.RGBA)

        // flip the buffer so its ready to read
        (buffer as Buffer).flip()

        content.load(
            TextureImage(
                width = decoder.width,
                height = decoder.height,
                glFormat = GL.RGBA,
                glType = GL.UNSIGNED_BYTE,
                pixels = buffer
            )
        )
        return content
    }

    actual fun readSound(filename: String): Content<Sound> {
        TODO("Not yet implemented")
    }
}
