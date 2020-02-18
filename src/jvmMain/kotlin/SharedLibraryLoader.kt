import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import java.util.zip.CRC32
import java.util.zip.ZipFile

/** Loads shared libraries from JAR files. Call [to load the][SharedLibraryLoader.load] */
class SharedLibraryLoader {
    companion object {
        var isWindows = System.getProperty("os.name").contains("Windows")
        var isLinux = System.getProperty("os.name").contains("Linux")
        var isMac = System.getProperty("os.name").contains("Mac")
        var isIos = false
        var isAndroid = false
        var isARM = System.getProperty("os.arch").startsWith("arm")
        var is64Bit =
            System.getProperty("os.arch") == "amd64" || System.getProperty("os.arch") == "x86_64"
        // JDK 8 only.
        var abi =
            if (System.getProperty("sun.arch.abi") != null) System.getProperty("sun.arch.abi") else ""
        var load = true
        /** Extracts the LWJGL native libraries from the classpath and sets the "org.lwjgl.librarypath" system property.  */
        @Synchronized
        fun load() {
            load(false)
        }

        /** Extracts the LWJGL native libraries from the classpath and sets the "org.lwjgl.librarypath" system property.  */
        @Synchronized
        fun load(disableOpenAL: Boolean, nativesJar: String? = null) {
            if (!load) return
            val loader = SharedLibraryLoader(nativesJar)
            var nativesDir: File? = null
            try {
                if (isWindows) {
                    nativesDir = loader.extractFile(
                        if (is64Bit) "lwjgl.dll" else "lwjgl32.dll",
                        null
                    ).parentFile
                    if (!disableOpenAL) loader.extractFile(
                        if (is64Bit) "OpenAL.dll" else "OpenAL32.dll",
                        nativesDir.name
                    )
                } else if (isMac) {
                    nativesDir = loader.extractFile("macos/x64/org/lwjgl/liblwjgl.dylib", null).parentFile
                    if (!disableOpenAL) loader.extractFile("libopenal.dylib", nativesDir.name)
                } else if (isLinux) {
                    nativesDir = loader.extractFile(
                        if (is64Bit) "liblwjgl.so" else "liblwjgl32.so",
                        null
                    ).parentFile
                    if (!disableOpenAL) loader.extractFile(
                        if (is64Bit) "libopenal.so" else "libopenal32.so",
                        nativesDir.name
                    )
                }
            } catch (ex: Throwable) {
                throw RuntimeException("Unable to extract LWJGL natives.", ex)
            }
            System.setProperty("org.lwjgl.librarypath", nativesDir!!.absolutePath)
            load = false
        }

        private val loadedLibraries = HashSet<String>()

        init {
            val vm = System.getProperty("java.runtime.name")
            if (vm != null && vm.contains("Android Runtime")) {
                isAndroid = true
                isWindows = false
                isLinux = false
                isMac = false
                is64Bit = false
            }
            if (!isAndroid && !isWindows && !isLinux && !isMac) {
                isIos = true
                is64Bit = false
            }
        }

        init { // Don't extract natives if using JWS.
            load = try {
                val method =
                    Class.forName("javax.jnlp.ServiceManager").getDeclaredMethod(
                        "lookup", *arrayOf<Class<*>>(
                            String::class.java
                        )
                    )
                method.invoke(null, "javax.jnlp.PersistenceService")
                false
            } catch (ex: Throwable) {
                true
            }
        }
    }

    private var nativesJar: String? = null

    constructor() {}
    /** Fetches the natives from the given natives jar file. Used for testing a shared lib on the fly.
     * @param nativesJar
     */
    constructor(nativesJar: String?) {
        this.nativesJar = nativesJar
    }

    /** Returns a CRC of the remaining bytes in the stream.  */
    fun crc(input: InputStream?): String {
        requireNotNull(input) { "input cannot be null." }
        val crc = CRC32()
        val buffer = ByteArray(4096)
        input.use {
            while (true) {
                val length = input.read(buffer)
                if (length == -1) break
                crc.update(buffer, 0, length)
            }
        }
        return crc.value.toString(16)
    }

    /** Maps a platform independent library name to a platform dependent name.  */
    fun mapLibraryName(libraryName: String): String {
        if (isWindows) return libraryName + if (is64Bit) "64.dll" else ".dll"
        if (isLinux) return "lib" + libraryName + (if (isARM) "arm$abi" else "") + if (is64Bit) "64.so" else ".so"
        return if (isMac) "lib" + libraryName + (if (is64Bit) "64.dylib" else ".dylib") else libraryName
    }

    /** Loads a shared library for the platform the application is running on.
     * @param libraryName The platform independent library name. If not contain a prefix (eg lib) or suffix (eg .dll).
     */
    @Synchronized
    fun load(libraryName: String) { // in case of iOS, things have been linked statically to the executable, bail out.
        var libraryName = libraryName
        if (isIos) return
        libraryName = mapLibraryName(libraryName)
        if (loadedLibraries.contains(libraryName)) return
        try {
            if (isAndroid) System.loadLibrary(libraryName) else loadFile(
                libraryName
            )
        } catch (ex: Throwable) {
            val nbBits = if (is64Bit) ", 64-bit" else ", 32-bit"
            throw RuntimeException(
                "Couldn't load shared library '$libraryName' for target: ${System.getProperty("os.name")}$nbBits",
                ex
            )
        }
        loadedLibraries.add(libraryName)
    }

    private fun readFile(path: String): InputStream {
        if (nativesJar == null) {
            return SharedLibraryLoader::class.java.getResourceAsStream("/$path")
                ?: throw RuntimeException("Unable to read file for extraction: $path")
        }
        // Read from JAR.
        var file: ZipFile? = null
        return try {
            file = ZipFile(nativesJar)
            val entry = file.getEntry(path) ?: throw RuntimeException("Couldn't find '$path' in JAR: $nativesJar")
            file.getInputStream(entry)
        } catch (ex: IOException) {
            throw RuntimeException("Error reading '$path' in JAR: $nativesJar", ex)
        } finally {
            if (file != null) {
                try {
                    file.close()
                } catch (e: IOException) {
                }
            }
        }
    }

    /** Extracts the specified file into the temp directory if it does not already exist or the CRC does not match. If file
     * extraction fails and the file exists at java.library.path, that file is returned.
     * @param sourcePath The file to extract from the classpath or JAR.
     * @param dirName The name of the subdirectory where the file will be extracted. If null, the file's CRC will be used.
     * @return The extracted file.
     */
    @Throws(IOException::class)
    fun extractFile(sourcePath: String, dirName: String?): File {
        var dirName = dirName
        return try {
            val sourceCrc = crc(readFile(sourcePath))
            if (dirName == null) dirName = sourceCrc
            val extractedFile = getExtractedFile(dirName, File(sourcePath).name)
            extractFile(sourcePath, sourceCrc, extractedFile)
        } catch (ex: RuntimeException) { // Fallback to file at java.library.path location, eg for applets.
            val file = File(System.getProperty("java.library.path"), sourcePath)
            if (file.exists()) return file
            throw ex
        }
    }

    /** Returns a path to a file that can be written. Tries multiple locations and verifies writing succeeds.  */
    private fun getExtractedFile(
        dirName: String?,
        fileName: String
    ): File { // Temp directory with username in path.
        val parentName =
            System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + dirName
        val idealFile = File(parentName, fileName)
        if (canWrite(idealFile)) return idealFile
        // System provided temp directory.
        try {
            var file = File.createTempFile(dirName, null)
            if (file.delete()) {
                file = File(file, fileName)
                if (canWrite(file)) return file
            }
        } catch (ignored: IOException) {
        }
        // User home.
        var file =
            File(System.getProperty("user.home") + "/.libgdx/" + dirName, fileName)
        if (canWrite(file)) return file
        // Relative directory.
        file = File(".temp/$dirName", fileName)
        return if (canWrite(file)) file else idealFile
        // Will likely fail, but we did our best.
    }

    /** Returns true if the parent directories of the file can be created and the file can be written.  */
    private fun canWrite(file: File): Boolean {
        val parent = file.parentFile
        val testFile: File
        testFile = if (file.exists()) {
            if (!file.canWrite() || !canExecute(file)) return false
            // Don't overwrite existing file just to check if we can write to directory.
            File(parent, UUID.randomUUID().toString())
        } else {
            parent.mkdirs()
            if (!parent.isDirectory) return false
            file
        }
        return try {
            FileOutputStream(testFile).close()
            if (!canExecute(testFile)) false else true
        } catch (ex: Throwable) {
            false
        } finally {
            testFile.delete()
        }
    }

    private fun canExecute(file: File): Boolean {
        try {
            val canExecute = File::class.java.getMethod("canExecute")
            if (canExecute.invoke(file) as Boolean) return true
            val setExecutable = File::class.java.getMethod(
                "setExecutable",
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType
            )
            setExecutable.invoke(file, true, false)
            return canExecute.invoke(file) as Boolean
        } catch (ignored: Exception) {
        }
        return false
    }

    @Throws(IOException::class)
    private fun extractFile(
        sourcePath: String,
        sourceCrc: String,
        extractedFile: File
    ): File {
        var extractedCrc: String? = null
        if (extractedFile.exists()) {
            try {
                extractedCrc = crc(FileInputStream(extractedFile))
            } catch (ignored: FileNotFoundException) {
            }
        }
        // If file doesn't exist or the CRC doesn't match, extract it to the temp dir.
        if (extractedCrc == null || extractedCrc != sourceCrc) {
            try {
                val input = readFile(sourcePath)
                extractedFile.parentFile.mkdirs()
                val output = FileOutputStream(extractedFile)
                val buffer = ByteArray(4096)
                while (true) {
                    val length = input.read(buffer)
                    if (length == -1) break
                    output.write(buffer, 0, length)
                }
                input.close()
                output.close()
            } catch (ex: IOException) {
                throw RuntimeException(
                    "Error extracting file: " + sourcePath + "\nTo: " + extractedFile.absolutePath,
                    ex
                )
            }
        }
        return extractedFile
    }

    /** Extracts the source file and calls System.load. Attemps to extract and load from multiple locations. Throws runtime
     * exception if all fail.  */
    private fun loadFile(sourcePath: String) {
        val sourceCrc = crc(readFile(sourcePath))
        val fileName = File(sourcePath).name
        // Temp directory with username in path.
        var file = File(
            System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + sourceCrc,
            fileName
        )
        val ex = loadFile(sourcePath, sourceCrc, file) ?: return
        // System provided temp directory.
        try {
            file = File.createTempFile(sourceCrc, null)
            if (file.delete() && loadFile(sourcePath, sourceCrc, file) == null) return
        } catch (ignored: Throwable) {
        }
        // User home.
        file = File(System.getProperty("user.home") + "/.libgdx/" + sourceCrc, fileName)
        if (loadFile(sourcePath, sourceCrc, file) == null) return
        // Relative directory.
        file = File(".temp/$sourceCrc", fileName)
        if (loadFile(sourcePath, sourceCrc, file) == null) return
        // Fallback to java.library.path location, eg for applets.
        file = File(System.getProperty("java.library.path"), sourcePath)
        if (file.exists()) {
            System.load(file.absolutePath)
            return
        }
        throw RuntimeException(ex)
    }

    /** @return null if the file was extracted and loaded.
     */
    private fun loadFile(sourcePath: String, sourceCrc: String, extractedFile: File): Throwable? {
        return try {
            System.load(extractFile(sourcePath, sourceCrc, extractedFile).absolutePath)
            null
        } catch (ex: Throwable) {
            ex.printStackTrace()
            ex
        }
    }
}
