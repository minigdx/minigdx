package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.logger.Logger
import de.matthiasmann.twl.utils.PNGDecoder
import fr.delthas.javamp3.Sound as Mp3Sound
import java.io.File
import java.nio.ByteBuffer

actual class PlatformFileHandler(actual val logger: Logger) {

    actual fun read(filename: String): Content<String> {
        val content = Content<String>(filename, logger)
        content.load(File(filename).readText())
        return content
    }

    actual fun readData(filename: String): Content<ByteArray> {
        val content = Content<ByteArray>(filename, logger)
        content.load(File(filename).readBytes())
        return content
    }

    actual fun readTextureImage(filename: String): Content<TextureImage> {

        val content = Content<TextureImage>(filename, logger)
        val file = File(filename)

        val decoder = PNGDecoder(file.inputStream())

        // create a byte buffer big enough to store RGBA values
        val buffer =
            ByteBuffer.allocateDirect(4 * decoder.width * decoder.height)

        // decode
        decoder.decode(buffer, decoder.width * 4, PNGDecoder.Format.RGBA)

        // flip the buffer so its ready to read
        buffer.flip()

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
        val (source, channels, frequency) = if (filename.endsWith(".mp3")) {
            val mp3Stream = Mp3Sound(File(filename).inputStream())
            val source = mp3Stream.readBytes().also { mp3Stream.close() }
            val channels = if (mp3Stream.isStereo) {
                2
            } else {
                1
            }
            Triple(source, channels, mp3Stream.samplingFrequency)
        } else {
            TODO("NOT IMPLEMENTED YET")
        }
        val buffer = ByteBuffer.allocateDirect(source.size)
        buffer.put(source)
        buffer.position(0)
        val content = Content<Sound>(filename, logger)
        content.load(Sound(buffer.asShortBuffer(), channels, frequency))
        return content
    }
}
