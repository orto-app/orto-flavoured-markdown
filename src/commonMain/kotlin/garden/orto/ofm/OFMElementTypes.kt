package garden.orto.ofm

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementType
import kotlin.jvm.JvmField

object OFMTokenTypes {
    @JvmField
    val ORTO_TAG: IElementType = MarkdownElementType("ORTO_TAG")
}

object OFMElementTypes {
    @JvmField
    val ORTO_TAG: IElementType = MarkdownElementType("ORTO_TAG", true)
}
