package garden.orto.ofm

import org.intellij.markdown.lexer.Compat.assert

const val OFM_TEST_KEY = "garden.orto.ofm.home"

const val MARKDOWN_TEST_DATA_PATH = "src/fileBasedTest/resources/data"

expect abstract class TestCase() {
    fun getName(): String
}

expect fun readFromFile(path: String): String

expect fun assertSameLinesWithFile(path: String, result: String)

expect fun getIntellijMarkdownHome(): String

val TestCase.testName: String
    get() {
        val name = getName()
        assert(name.startsWith("test"))
        return name.substring("test".length).replaceFirstChar { it.lowercase() }
    }
