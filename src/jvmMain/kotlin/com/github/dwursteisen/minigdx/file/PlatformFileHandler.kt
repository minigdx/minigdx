package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.logger.Logger
import de.matthiasmann.twl.utils.PNGDecoder
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.Base64
import fr.delthas.javamp3.Sound as Mp3Sound

actual class PlatformFileHandler(actual val logger: Logger) {

    actual fun read(filename: String): Content<String> {
        val content = Content<String>(filename, logger)

        // Check if the resource is embedded in the jar
        val fromJar = PlatformFileHandler::class.java.getResourceAsStream("/$filename")
        val text = fromJar?.readBytes()?.let { String(it) } ?: File(filename).readText()
        content.load(text)
        return content
    }

    actual fun readData(filename: String): Content<ByteArray> {
        val content = Content<ByteArray>(filename, logger)
        // Check if the resource is embedded in the jar
        val fromJar = PlatformFileHandler::class.java.getResource("/$filename")
        val bytes = fromJar?.readBytes() ?: File(filename).readBytes()
        content.load(bytes)
        return content
    }

    actual fun readTextureImage(filename: String): Content<TextureImage> {
        val content = Content<TextureImage>(filename, logger)

        // lock first in the jar before checking on the filesystem.
        val stream = PlatformFileHandler::class.java.getResource("/$filename")?.openStream()
            ?: File(filename).inputStream()

        val texture = createTextureImage(stream)
        content.load(texture)
        return content
    }

    actual fun decodeTextureImage(filename: String, data: ByteArray): Content<TextureImage> {
        val content = Content<TextureImage>(filename, logger)

        val texture = createTextureImage(Base64.getDecoder().wrap(ByteArrayInputStream(data)))
        content.load(texture)
        return content
    }

    private fun createTextureImage(stream: InputStream): TextureImage {
        val decoder = PNGDecoder(stream)

        // create a byte buffer big enough to store RGBA values
        val buffer =
            ByteBuffer.allocateDirect(4 * decoder.width * decoder.height)

        // decode
        decoder.decode(buffer, decoder.width * 4, PNGDecoder.Format.RGBA)

        // flip the buffer so its ready to read
        (buffer as Buffer).flip()

        val texture = TextureImage(
            width = decoder.width,
            height = decoder.height,
            glFormat = GL.RGBA,
            glType = GL.UNSIGNED_BYTE,
            pixels = buffer
        )
        return texture
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
        (buffer as Buffer).position(0)
        val content = Content<Sound>(filename, logger)
        content.load(Sound(buffer.asShortBuffer(), channels, frequency))
        return content
    }
}
