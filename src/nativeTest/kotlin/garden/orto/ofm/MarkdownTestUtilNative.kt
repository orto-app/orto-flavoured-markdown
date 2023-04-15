package garden.orto.ofm

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.*
import kotlin.test.assertEquals

actual fun readFromFile(path: String): String {
    val file = requireNotNull(fopen(path, "r")) { "Invalid path $path" }
    val bytes = mutableListOf<Byte>()
    try {
        while (true) {
            val c = fgetc(file)
            if (c == EOF) break
            bytes.add(c.toByte())
        }
    } finally {
        fclose(file)
    }
    return bytes.toByteArray().decodeToString()
}

actual fun assertSameLinesWithFile(path: String, result: String) {
    val fileText = readFromFile(path)
    assertEquals(fileText, result)
}

private val intellijMarkdownHome: Lazy<String> = lazy {
    memScoped {
        val buffer = allocArray<ByteVar>(PATH_MAX)
        var dir = getcwd(buffer, PATH_MAX)?.toKString()?.replace("\\", "/") ?: error("could not get cwd")
        while (access(dir, F_OK) == -1) {
            dir = dir.substringBeforeLast("/")
            if (dir.isEmpty()) {
                error("could not find repo root. cwd=${buffer.toKString()}")
            }
        }
        dir
    }
}

actual fun getIntellijMarkdownHome(): String {
    return intellijMarkdownHome.value
}

