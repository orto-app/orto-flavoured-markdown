package garden.orto.ofm

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.LeafASTNode
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownParsingTest : TestCase() {

    private fun defaultTest() {
        val src = readFromFile(getTestDataPath() + "/" + testName + ".md")
        val result = getParsedTreeText(src)

        assertSameLinesWithFile(getTestDataPath() + "/" + testName + ".txt", result)
    }

    private fun fileTestCase(path: String, tags: Set<String>) {
        val src = readFromFile(getTestDataPath() + path)
        stringTestCase(src, tags)
    }

    private fun stringTestCase(src: String, expected: Set<String>) {
        val actual = getTags(src)
        assertEquals(expected, actual)
    }

    private fun getTestDataPath(): String {
        return getOFMMarkdownHome() + "/${MARKDOWN_TEST_DATA_PATH}/parser/"
    }


    private fun getParsedTreeText(inputText: String): String {
        val tree = parseMarkdown(inputText)
        return treeToStr(inputText, tree)
    }

    private fun treeToStr(src: String, tree: ASTNode): String {
        return treeToStr(src, tree, StringBuilder(), 0).toString()
    }

    private fun treeToStr(
        src: String,
        tree: ASTNode,
        sb: StringBuilder,
        depth: Int
    ): StringBuilder {
        if (sb.isNotEmpty()) {
            sb.append('\n')
        }
        repeat(depth * 2) { sb.append(' '); }

        sb.append(tree.type.toString())
        if (tree is LeafASTNode) {
            val str = src.substring(tree.startOffset, tree.endOffset)
            sb.append("('").append(str.replace("\n", "\\n")).append("')")
        }
        for (child in tree.children) {
            treeToStr(src, child, sb, depth + 1)
        }

        return sb
    }

    @Test
    fun testEmpty() {
        assertEquals("Markdown:MARKDOWN_FILE", getParsedTreeText(""))
    }

    @Test
    fun testTagsIntegration() =
        stringTestCase("# HEADER 1\n" +
                "\n" +
                "Paragrafo di #parole, ma con dei #tag,#anche fasulli #in/mezzo. Dovremmo mettere #tanti#, esempi di \n" +
                "\n" +
                "- modo\n" +
                "- da\n" +
                "- fare\n" +
                "- #test/esaus#tivi",
            setOf(",", "parole,", "tag,", "anche", "in/mezzo.", "tanti", "test/esaus", "tivi"))

    @Test
    fun testGetTagsEmptyTag() = stringTestCase(
        "\n Normale paragrafo con un ##tag nullo",
        setOf("tag")
    )

    @Test
    fun testGetTagsEmoji() = stringTestCase(
        "\n Normale paragrafo con un #\uD83C\uDFF7\uFE0F/composito #tag emoji",
        setOf("\uD83C\uDFF7\uFE0F/composito", "tag")
    )

    @Test
    fun testGetTagsInHeaders() = stringTestCase(
        "\n# Normale header\n## Ciaone #questo/invece_è un he#ader con dei tag",
        setOf("questo/invece_è", "ader")
    )

    @Test
    fun testGetTagsInLists() = stringTestCase(
        "- element\n - ele##ment\n- #è£ement\n\n1. #\uD83E\uDD91 is a squid\n2. \uD83D\uDC19 is an #octopus\n3. #\uD83D\uDC7Eè un mostro alieno",
        setOf("ment", "è£ement", "\uD83E\uDD91", "octopus", "\uD83D\uDC7Eè")
    )

    @Test
    fun testGetTagsInTableHeaders() = stringTestCase(
        "| First Header # | Second #Heàder |\n" +
                "| ------------- | ------------- |\n" +
                "| Content | Content |\n" +
                "| Content | Content |",
        setOf("Heàder")
    )

    @Test
    fun testGetTagsInTableCells() = stringTestCase(
        "| First Header | Second Heàder |\n" +
                "| ------------- | ------------- |\n" +
                "| Con#tent | Content |\n" +
                "| Content | Co#ntent |",
        setOf("tent", "ntent")
    )

    @Test
    fun testGetTagsInLinksDescriptions() = stringTestCase(
        "[Nice #web§ite ](https://orto.garden)",
        setOf("web§ite")
    )
}
