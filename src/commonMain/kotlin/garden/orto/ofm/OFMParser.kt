package garden.orto.ofm

import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

val flavour = OrtoFlavourDescriptor()
val parser = MarkdownParser(flavour)

fun walkForTags(root: ASTNode): List<ASTNode>  {
    val result = if (root.type == MarkdownTokenTypes.TEXT) listOf(root) else listOf()
    return result + root.children.flatMap(::walkForTags)
}

fun getTags(src: String): Set<ASTNode> {
    val parsedTree = parseMarkdown(src)
    return walkForTags(parsedTree).toSet()
}

fun parseMarkdown(src: String) = parser.buildMarkdownTreeFromString(src)
fun generateHtml(src: String) = generateHtml(src, parseMarkdown(src))
fun generateHtml(src: String, parsedTree: ASTNode) = HtmlGenerator(src, parsedTree, flavour).generateHtml()
