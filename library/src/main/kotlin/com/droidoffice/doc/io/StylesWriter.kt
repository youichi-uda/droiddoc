package com.droidoffice.doc.io

import com.droidoffice.doc.core.BuiltInStyle

/**
 * Generates word/styles.xml with built-in styles.
 */
internal object StylesWriter {

    fun buildStylesXml(): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        appendLine("""<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">""")

        // Default style
        appendLine("""  <w:docDefaults>""")
        appendLine("""    <w:rPrDefault>""")
        appendLine("""      <w:rPr>""")
        appendLine("""        <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri" w:eastAsia="MS Gothic"/>""")
        appendLine("""        <w:sz w:val="22"/>""")
        appendLine("""        <w:szCs w:val="22"/>""")
        appendLine("""      </w:rPr>""")
        appendLine("""    </w:rPrDefault>""")
        appendLine("""    <w:pPrDefault>""")
        appendLine("""      <w:pPr>""")
        appendLine("""        <w:spacing w:after="160" w:line="259" w:lineRule="auto"/>""")
        appendLine("""      </w:pPr>""")
        appendLine("""    </w:pPrDefault>""")
        appendLine("""  </w:docDefaults>""")

        // Normal
        writeParaStyle("Normal", "Normal", null, null, null, false, false)

        // Headings
        for (i in 1..6) {
            val size = when (i) { 1 -> 32; 2 -> 26; 3 -> 24; 4 -> 22; 5 -> 22; else -> 22 }
            val bold = i <= 4
            writeParaStyle("Heading$i", "heading $i", "Normal", size, null, bold, false)
        }

        // Title
        writeParaStyle("Title", "Title", "Normal", 56, null, false, false)

        // Subtitle
        writeParaStyle("Subtitle", "Subtitle", "Normal", 28, "5B9BD5", false, true)

        // Quote
        writeParaStyle("Quote", "Quote", "Normal", null, "404040", false, true)

        // Hyperlink character style
        appendLine("""  <w:style w:type="character" w:styleId="Hyperlink">""")
        appendLine("""    <w:name w:val="Hyperlink"/>""")
        appendLine("""    <w:rPr>""")
        appendLine("""      <w:color w:val="0563C1"/>""")
        appendLine("""      <w:u w:val="single"/>""")
        appendLine("""    </w:rPr>""")
        appendLine("""  </w:style>""")

        // ListParagraph
        writeParaStyle("ListParagraph", "List Paragraph", "Normal", null, null, false, false)

        appendLine("""</w:styles>""")
    }

    private fun StringBuilder.writeParaStyle(
        styleId: String,
        name: String,
        basedOn: String?,
        fontSize: Int?,
        colorHex: String?,
        bold: Boolean,
        italic: Boolean,
    ) {
        appendLine("""  <w:style w:type="paragraph" w:styleId="$styleId">""")
        appendLine("""    <w:name w:val="$name"/>""")
        basedOn?.let { appendLine("""    <w:basedOn w:val="$it"/>""") }
        val hasRunProps = fontSize != null || colorHex != null || bold || italic
        if (hasRunProps) {
            appendLine("""    <w:rPr>""")
            fontSize?.let {
                val halfPt = it * 2
                appendLine("""      <w:sz w:val="$halfPt"/>""")
                appendLine("""      <w:szCs w:val="$halfPt"/>""")
            }
            colorHex?.let { appendLine("""      <w:color w:val="$it"/>""") }
            if (bold) appendLine("""      <w:b/>""")
            if (italic) appendLine("""      <w:i/>""")
            appendLine("""    </w:rPr>""")
        }
        appendLine("""  </w:style>""")
    }
}
