package garden.orto.ofm

import garden.orto.ofm.flavours.ofm.OrtoFlavourDescriptor
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

val ofmFlavour = OrtoFlavourDescriptor()
val ofmParser = MarkdownParser(ofmFlavour)

fun walkForTags(root: ASTNode): List<ASTNode> {
    val result = if (root.type == OFMTokenTypes.ORTO_TAG) listOf(root) else listOf()
    return result + root.children.flatMap(::walkForTags)
}

fun getTags(src: String): Set<String> {
    val parsedTree = parseMarkdown(src)
    return walkForTags(parsedTree).map { node -> src.substring(node.startOffset + 1, node.endOffset) }.toSet()
}

fun parseMarkdown(src: String) = ofmParser.buildMarkdownTreeFromString(src)

fun generateHtml(src: String) = generateHtml(src, parseMarkdown(src))

fun generateHtml(src: String, parsedTree: ASTNode) = HtmlGenerator(src, parsedTree, ofmFlavour).generateHtml()
