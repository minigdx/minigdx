package com.github.dwursteisen.minigdx.graphics.compilers

import com.github.dwursteisen.minigdx.file.AngelCodeLoader
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.FontLoader
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.file.TextureLoader
import com.github.dwursteisen.minigdx.text.AngelCode
import com.github.dwursteisen.minigdx.text.Font
import com.github.dwursteisen.minigdx.texture.Texture
import createLogger
import org.junit.Test

class TextPrimitiveCompilerTest {

    @Test
    fun currentLineInPixel() {
        val logger = createLogger()
        val fileHandler = FileHandler(
            PlatformFileHandler(logger), loaders = mapOf(
                Font::class to FontLoader(),
                Texture::class to TextureLoader(),
                AngelCode::class to AngelCodeLoader()
            ), logger = logger
        )

        val font: Font by fileHandler.get("src/jvmTest/resources/pt_font", Font::class)
        // val result = TextPrimitiveCompiler().currentLineInPixel("text", font)

        // assertEquals(70, result)
    }
}
