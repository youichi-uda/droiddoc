package com.droidoffice.doc.io

import com.droidoffice.core.drawingml.BorderStyle
import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import com.droidoffice.core.ooxml.OoxmlPackage
import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.HeaderFooter
import com.droidoffice.doc.core.HeaderFooterType
import com.droidoffice.doc.core.LineSpacingRule
import com.droidoffice.doc.core.ListType
import com.droidoffice.doc.core.PageOrientation
import com.droidoffice.doc.core.Paragraph
import com.droidoffice.doc.core.ParagraphAlignment
import com.droidoffice.doc.core.Run
import com.droidoffice.doc.core.Table
import com.droidoffice.doc.core.TableCell
import com.droidoffice.doc.core.TableRow
import java.io.OutputStream

/**
 * Writes a Document to .docx format (OOXML WordprocessingML).
 */
object DocxWriter {

    fun write(document: Document, output: OutputStream) {
        val pkg = OoxmlPackage.create()

        // Collect hyperlinks across all paragraphs
        val hyperlinkRels = mutableListOf<Pair<String, String>>() // relId to url
        var relIdCounter = 1

        // Write images and collect rels
        val imageRelIds = if (document.pictures.isNotEmpty()) {
            DrawingWriter.writeImages(pkg, document.pictures)
        } else {
            emptyList()
        }

        // Assign relIds for images
        val imageRelMap = mutableMapOf<Int, String>()
        for ((i, relId) in imageRelIds.withIndex()) {
            imageRelMap[i] = "rIdImg${i + 1}"
            relIdCounter++
        }

        // Collect hyperlink rels from all paragraphs
        val hyperlinkRelMap = mutableMapOf<String, String>() // url -> relId
        for (paragraph in document.paragraphs) {
            for (link in paragraph.hyperlinks) {
                if (link.url !in hyperlinkRelMap) {
                    val relId = "rIdLink${relIdCounter++}"
                    hyperlinkRelMap[link.url] = relId
                    hyperlinkRels.add(relId to link.url)
                }
            }
        }

        // Check if numbering is needed
        val hasLists = document.paragraphs.any { it.listStyle != null }

        // Header/footer parts
        val headerFooterRels = mutableListOf<Triple<String, String, String>>() // relId, type, target
        val section = document.defaultSection
        var hfCounter = 1

        for ((type, hf) in section.headers) {
            val num = hfCounter++
            val relId = "rIdHdr$num"
            val target = "header$num.xml"
            headerFooterRels.add(Triple(relId, "header", target))
            pkg.setPart("word/$target", buildHeaderFooterXml(hf, "hdr").toByteArray())
        }
        for ((type, hf) in section.footers) {
            val num = hfCounter++
            val relId = "rIdFtr$num"
            val target = "footer$num.xml"
            headerFooterRels.add(Triple(relId, "footer", target))
            pkg.setPart("word/$target", buildHeaderFooterXml(hf, "ftr").toByteArray())
        }

        pkg.setPart("[Content_Types].xml", buildContentTypes(document, hasLists, headerFooterRels).toByteArray())
        pkg.setPart("_rels/.rels", buildTopRels().toByteArray())
        pkg.setPart("word/document.xml", buildDocumentXml(document, imageRelMap, hyperlinkRelMap, headerFooterRels).toByteArray())
        pkg.setPart("word/_rels/document.xml.rels", buildDocumentRels(document, imageRelIds, hyperlinkRels, hasLists, headerFooterRels).toByteArray())
        pkg.setPart("word/styles.xml", StylesWriter.buildStylesXml().toByteArray())

        if (hasLists) {
            pkg.setPart("word/numbering.xml", NumberingWriter.buildNumberingXml(
                NumberingWriter.BULLET_NUM_ID, NumberingWriter.NUMBERED_NUM_ID
            ).toByteArray())
        }

        pkg.writeTo(output)
    }

    private fun buildContentTypes(document: Document, hasLists: Boolean, headerFooterRels: List<Triple<String, String, String>> = emptyList()): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        appendLine("""<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">""")
        appendLine("""  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>""")
        appendLine("""  <Default Extension="xml" ContentType="application/xml"/>""")
        appendLine("""  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>""")
        appendLine("""  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>""")
        if (hasLists) {
            appendLine("""  <Override PartName="/word/numbering.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml"/>""")
        }
        for ((_, type, target) in headerFooterRels) {
            val ct = if (type == "header") "header" else "footer"
            appendLine("""  <Override PartName="/word/$target" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.$ct+xml"/>""")
        }
        val imageFormats = document.pictures.map { it.format }.toSet()
        for (fmt in imageFormats) {
            appendLine("""  <Default Extension="${fmt.extension}" ContentType="${fmt.contentType}"/>""")
        }
        appendLine("</Types>")
    }

    private fun buildTopRels(): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        appendLine("""<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">""")
        appendLine("""  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>""")
        appendLine("</Relationships>")
    }

    private fun buildDocumentRels(
        document: Document,
        imageRelIds: List<String>,
        hyperlinkRels: List<Pair<String, String>>,
        hasLists: Boolean,
        headerFooterRels: List<Triple<String, String, String>> = emptyList(),
    ): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        appendLine("""<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">""")
        appendLine("""  <Relationship Id="rIdStyles" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>""")
        for ((i, relId) in imageRelIds.withIndex()) {
            val ext = document.pictures[i].format.extension
            appendLine("""  <Relationship Id="$relId" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image" Target="media/image${i + 1}.$ext"/>""")
        }
        for ((relId, url) in hyperlinkRels) {
            appendLine("""  <Relationship Id="$relId" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink" Target="${escapeXml(url)}" TargetMode="External"/>""")
        }
        if (hasLists) {
            appendLine("""  <Relationship Id="rIdNumbering" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/numbering" Target="numbering.xml"/>""")
        }
        for ((relId, type, target) in headerFooterRels) {
            val relType = if (type == "header") "header" else "footer"
            appendLine("""  <Relationship Id="$relId" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/$relType" Target="$target"/>""")
        }
        appendLine("</Relationships>")
    }

    internal fun buildDocumentXml(
        document: Document,
        imageRelMap: Map<Int, String> = emptyMap(),
        hyperlinkRelMap: Map<String, String> = emptyMap(),
        headerFooterRels: List<Triple<String, String, String>> = emptyList(),
    ): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        appendLine("""<w:document xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas" xmlns:mo="http://schemas.microsoft.com/office/mac/office/2008/main" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:mv="urn:schemas-microsoft-com:mac:vml" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup" xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk" xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml" xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape" mc:Ignorable="w14 wp14">""")
        appendLine("  <w:body>")

        var drawingId = 1
        for (element in document.bodyElements) {
            when (element) {
                is Paragraph -> writeParagraph(this, element, hyperlinkRelMap)
                is Table -> writeTable(this, element)
            }
        }

        // Inline images (added after all paragraphs as separate paragraphs)
        for ((index, picture) in document.pictures.withIndex()) {
            val relId = imageRelMap[index] ?: continue
            appendLine("    <w:p>")
            append(DrawingWriter.buildInlineDrawingXml(picture, relId, drawingId++))
            appendLine("    </w:p>")
        }

        // Section properties (page setup + header/footer refs)
        writeSectionProperties(this, document, headerFooterRels)

        appendLine("  </w:body>")
        appendLine("</w:document>")
    }

    internal fun writeParagraph(sb: StringBuilder, paragraph: Paragraph, hyperlinkRelMap: Map<String, String> = emptyMap()) {
        sb.appendLine("    <w:p>")

        // Paragraph properties
        val ps = paragraph.paragraphStyle
        val alignment = paragraph.alignment
        val hasList = paragraph.listStyle != null
        val hasParaProps = alignment != ParagraphAlignment.LEFT || paragraph.styleId != null ||
            ps.indentLeft != null || ps.indentRight != null || ps.indentFirstLine != null ||
            ps.indentHanging != null || ps.lineSpacing != null || ps.spaceBefore != null || ps.spaceAfter != null ||
            hasList
        if (hasParaProps) {
            sb.appendLine("      <w:pPr>")
            paragraph.styleId?.let { sb.appendLine("""        <w:pStyle w:val="${escapeXml(it)}"/>""") }

            // List numbering
            paragraph.listStyle?.let { ls ->
                val numId = when (ls.type) {
                    ListType.BULLET -> NumberingWriter.BULLET_NUM_ID
                    ListType.NUMBERED -> NumberingWriter.NUMBERED_NUM_ID
                }
                sb.appendLine("""        <w:numPr>""")
                sb.appendLine("""          <w:ilvl w:val="${ls.level}"/>""")
                sb.appendLine("""          <w:numId w:val="$numId"/>""")
                sb.appendLine("""        </w:numPr>""")
            }

            // Spacing
            val hasSpacing = ps.spaceBefore != null || ps.spaceAfter != null || ps.lineSpacing != null
            if (hasSpacing) {
                sb.append("        <w:spacing")
                ps.spaceBefore?.let { sb.append(""" w:before="$it"""") }
                ps.spaceAfter?.let { sb.append(""" w:after="$it"""") }
                ps.lineSpacing?.let {
                    sb.append(""" w:line="$it"""")
                    sb.append(""" w:lineRule="${ps.lineSpacingRule.toOoxml()}"""")
                }
                sb.appendLine("/>")
            }

            // Indentation
            val hasIndent = ps.indentLeft != null || ps.indentRight != null || ps.indentFirstLine != null || ps.indentHanging != null
            if (hasIndent) {
                sb.append("        <w:ind")
                ps.indentLeft?.let { sb.append(""" w:left="$it"""") }
                ps.indentRight?.let { sb.append(""" w:right="$it"""") }
                ps.indentFirstLine?.let { sb.append(""" w:firstLine="$it"""") }
                ps.indentHanging?.let { sb.append(""" w:hanging="$it"""") }
                sb.appendLine("/>")
            }

            if (alignment != ParagraphAlignment.LEFT) {
                sb.appendLine("""        <w:jc w:val="${alignment.toOoxml()}"/>""")
            }
            sb.appendLine("      </w:pPr>")
        }

        // Runs
        for (run in paragraph.runs) {
            writeRun(sb, run)
        }

        // Hyperlinks
        for (link in paragraph.hyperlinks) {
            val relId = hyperlinkRelMap[link.url]
            if (relId != null) {
                sb.appendLine("""      <w:hyperlink r:id="$relId">""")
                sb.appendLine("        <w:r>")
                sb.appendLine("          <w:rPr>")
                sb.appendLine("""            <w:rStyle w:val="Hyperlink"/>""")
                sb.appendLine("          </w:rPr>")
                sb.appendLine("""          <w:t xml:space="preserve">${escapeXml(link.text)}</w:t>""")
                sb.appendLine("        </w:r>")
                sb.appendLine("      </w:hyperlink>")
            }
        }

        sb.appendLine("    </w:p>")
    }

    internal fun writeRun(sb: StringBuilder, run: Run) {
        sb.appendLine("      <w:r>")

        // Run properties
        val style = run.style
        val hasRunProps = style.bold || style.italic || style.underline != UnderlineStyle.NONE ||
            style.strikethrough || style.fontName != null || style.fontSize != null || style.color != null
        if (hasRunProps) {
            sb.appendLine("        <w:rPr>")
            style.fontName?.let {
                sb.appendLine("""          <w:rFonts w:ascii="${escapeXml(it)}" w:hAnsi="${escapeXml(it)}" w:eastAsia="${escapeXml(it)}"/>""")
            }
            if (style.bold) sb.appendLine("          <w:b/>")
            if (style.italic) sb.appendLine("          <w:i/>")
            if (style.strikethrough) sb.appendLine("          <w:strike/>")
            style.color?.let { color ->
                if (color is OfficeColor.Rgb) {
                    sb.appendLine("""          <w:color w:val="${color.toHex()}"/>""")
                }
            }
            style.fontSize?.let { size ->
                // Word stores font size in half-points
                val halfPoints = (size * 2).toInt()
                sb.appendLine("""          <w:sz w:val="$halfPoints"/>""")
                sb.appendLine("""          <w:szCs w:val="$halfPoints"/>""")
            }
            if (style.underline != UnderlineStyle.NONE) {
                val ulVal = when (style.underline) {
                    UnderlineStyle.SINGLE -> "single"
                    UnderlineStyle.DOUBLE -> "double"
                    else -> "single"
                }
                sb.appendLine("""          <w:u w:val="$ulVal"/>""")
            }
            sb.appendLine("        </w:rPr>")
        }

        sb.appendLine("""        <w:t xml:space="preserve">${escapeXml(run.text)}</w:t>""")
        sb.appendLine("      </w:r>")
    }

    internal fun writeTable(sb: StringBuilder, table: Table) {
        sb.appendLine("    <w:tbl>")
        sb.appendLine("      <w:tblPr>")
        sb.appendLine("""        <w:tblStyle w:val="TableGrid"/>""")
        sb.appendLine("""        <w:tblW w:w="0" w:type="auto"/>""")
        sb.appendLine("        <w:tblBorders>")
        writeBorder(sb, "top", table.borderTop.style)
        writeBorder(sb, "left", table.borderLeft.style)
        writeBorder(sb, "bottom", table.borderBottom.style)
        writeBorder(sb, "right", table.borderRight.style)
        writeBorder(sb, "insideH", table.borderInsideH.style)
        writeBorder(sb, "insideV", table.borderInsideV.style)
        sb.appendLine("        </w:tblBorders>")
        sb.appendLine("      </w:tblPr>")

        for (row in table.rows) {
            sb.appendLine("      <w:tr>")
            row.height?.let {
                sb.appendLine("""        <w:trPr><w:trHeight w:val="$it"/></w:trPr>""")
            }
            for (cell in row.cells) {
                sb.appendLine("        <w:tc>")
                sb.appendLine("          <w:tcPr>")
                cell.width?.let { sb.appendLine("""            <w:tcW w:w="$it" w:type="dxa"/>""") }
                if (cell.gridSpan > 1) sb.appendLine("""            <w:gridSpan w:val="${cell.gridSpan}"/>""")
                cell.verticalMerge?.let { sb.appendLine("""            <w:vMerge w:val="$it"/>""") }
                cell.shadingColor?.let { color ->
                    if (color is OfficeColor.Rgb) {
                        sb.appendLine("""            <w:shd w:val="clear" w:fill="${color.toHex()}"/>""")
                    }
                }
                sb.appendLine("          </w:tcPr>")
                if (cell.paragraphs.isEmpty()) {
                    sb.appendLine("          <w:p/>")
                } else {
                    for (para in cell.paragraphs) {
                        writeParagraph(sb, para)
                    }
                }
                sb.appendLine("        </w:tc>")
            }
            sb.appendLine("      </w:tr>")
        }

        sb.appendLine("    </w:tbl>")
    }

    private fun writeBorder(sb: StringBuilder, name: String, style: BorderStyle) {
        val sz = when (style) {
            BorderStyle.NONE -> { sb.appendLine("""          <w:$name w:val="none" w:sz="0" w:space="0"/>"""); return }
            BorderStyle.THIN -> 4
            BorderStyle.MEDIUM -> 12
            BorderStyle.THICK -> 24
            BorderStyle.DOUBLE -> 4
            BorderStyle.DASHED -> 4
            BorderStyle.DOTTED -> 4
            else -> 4
        }
        val ooxmlStyle = when (style) {
            BorderStyle.DOUBLE -> "double"
            BorderStyle.DASHED -> "dashed"
            BorderStyle.DOTTED -> "dotted"
            else -> "single"
        }
        sb.appendLine("""          <w:$name w:val="$ooxmlStyle" w:sz="$sz" w:space="0"/>""")
    }

    internal fun writeSectionProperties(sb: StringBuilder, document: Document, headerFooterRels: List<Triple<String, String, String>> = emptyList()) {
        val ps = document.pageSetup
        sb.appendLine("    <w:sectPr>")
        // Header/footer references
        for ((relId, type, _) in headerFooterRels) {
            val tag = if (type == "header") "headerReference" else "footerReference"
            sb.appendLine("""      <w:$tag w:type="default" r:id="$relId"/>""")
        }
        sb.append("      <w:pgSz w:w=\"${ps.effectiveWidth}\" w:h=\"${ps.effectiveHeight}\"")
        if (ps.orientation == PageOrientation.LANDSCAPE) {
            sb.append(" w:orient=\"landscape\"")
        }
        sb.appendLine("/>")
        sb.appendLine("      <w:pgMar w:top=\"${ps.marginTop}\" w:right=\"${ps.marginRight}\" w:bottom=\"${ps.marginBottom}\" w:left=\"${ps.marginLeft}\" w:header=\"${ps.marginHeader}\" w:footer=\"${ps.marginFooter}\"/>")
        sb.appendLine("    </w:sectPr>")
    }

    private fun buildHeaderFooterXml(hf: HeaderFooter, tag: String): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        appendLine("""<w:$tag xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">""")
        for (para in hf.paragraphs) {
            writeParagraph(this, para)
        }
        if (hf.paragraphs.isEmpty()) {
            appendLine("  <w:p/>")
        }
        appendLine("</w:$tag>")
    }

    internal fun escapeXml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}
