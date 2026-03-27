package com.droidoffice.doc.io

import com.droidoffice.core.ooxml.OoxmlPackage
import com.droidoffice.doc.drawing.Picture

/**
 * Writes image parts into the OOXML package.
 */
internal object DrawingWriter {

    fun writeImages(pkg: OoxmlPackage, pictures: List<Picture>): List<String> {
        val relIds = mutableListOf<String>()
        for ((index, picture) in pictures.withIndex()) {
            val imageNum = index + 1
            val path = "word/media/image$imageNum.${picture.format.extension}"
            pkg.setPart(path, picture.data)
            relIds.add("rIdImg$imageNum")
        }
        return relIds
    }

    fun buildInlineDrawingXml(picture: Picture, relId: String, drawingId: Int): String = buildString {
        appendLine("      <w:r>")
        appendLine("        <w:drawing>")
        appendLine("          <wp:inline distT=\"0\" distB=\"0\" distL=\"0\" distR=\"0\">")
        appendLine("            <wp:extent cx=\"${picture.widthEmu}\" cy=\"${picture.heightEmu}\"/>")
        appendLine("            <wp:docPr id=\"$drawingId\" name=\"Picture $drawingId\" descr=\"${DocxWriter.escapeXml(picture.description)}\"/>")
        appendLine("            <a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">")
        appendLine("              <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">")
        appendLine("                <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">")
        appendLine("                  <pic:nvPicPr>")
        appendLine("                    <pic:cNvPr id=\"$drawingId\" name=\"Picture $drawingId\"/>")
        appendLine("                    <pic:cNvPicPr/>")
        appendLine("                  </pic:nvPicPr>")
        appendLine("                  <pic:blipFill>")
        appendLine("                    <a:blip r:embed=\"$relId\"/>")
        appendLine("                    <a:stretch><a:fillRect/></a:stretch>")
        appendLine("                  </pic:blipFill>")
        appendLine("                  <pic:spPr>")
        appendLine("                    <a:xfrm>")
        appendLine("                      <a:off x=\"0\" y=\"0\"/>")
        appendLine("                      <a:ext cx=\"${picture.widthEmu}\" cy=\"${picture.heightEmu}\"/>")
        appendLine("                    </a:xfrm>")
        appendLine("                    <a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom>")
        appendLine("                  </pic:spPr>")
        appendLine("                </pic:pic>")
        appendLine("              </a:graphicData>")
        appendLine("            </a:graphic>")
        appendLine("          </wp:inline>")
        appendLine("        </w:drawing>")
        appendLine("      </w:r>")
    }
}
