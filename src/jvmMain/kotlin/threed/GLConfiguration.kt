package threed

import SharedLibraryLoader
import java.io.File
import java.net.URLClassLoader

typealias Pixels = Int

actual class GLConfiguration(
    val name: String,
    val width: Pixels,
    val height: Pixels
)

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private enum class Classifier(val exts: String) {
        WINDOWS("windows"), LINUX("linux"), MACOS("macos")
    }

    internal actual fun createContext(): GL {
        // Find the ljwgl jar.
        val classifier = computeJarClassifier()
        val jar = findJar(classifier)

        SharedLibraryLoader.load(disableOpenAL = true, nativesJar = jar.absolutePath)
        return LwjglGL(canvas = Canvas(configuration.width, configuration.height))
    }

    private fun findJar(classifier: Classifier): File {
        val cl = ClassLoader.getSystemClassLoader()

        val jar: File = (cl as URLClassLoader).urLs
            .map { File(it.toURI()) }
            .filter { it.name.startsWith("lwjgl") }
            .first { it.nameWithoutExtension.endsWith("natives-${classifier.exts}") }

        return jar
    }

    private fun computeJarClassifier(): Classifier {
        var isWindows = System.getProperty("os.name").contains("Windows")
        var isLinux = System.getProperty("os.name").contains("Linux")
        var isMac = System.getProperty("os.name").contains("Mac")

        return if (isWindows) Classifier.WINDOWS
        else if (isLinux) Classifier.LINUX
        else if (isMac) Classifier.MACOS
        else throw IllegalArgumentException("Not found what is the actual system.")
    }

    actual fun run(gameFactory: () -> Game) {
        // TODO
    }
}
