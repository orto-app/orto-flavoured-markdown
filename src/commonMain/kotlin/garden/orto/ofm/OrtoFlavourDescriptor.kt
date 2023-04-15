package garden.orto.ofm

import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.makeXssSafeDestination


/**
 * Orto Markdown based flavour, to be used as a base for other flavours.
 *
 * @param useSafeLinks `true` if all rendered links should be checked for XSS and `false` otherwise.
 * See [makeXssSafeDestination]
 */
class OrtoFlavourDescriptor(useSafeLinks: Boolean = true) : GFMFlavourDescriptor(useSafeLinks)