package com.droidoffice.doc.io

import com.droidoffice.doc.core.ListType

/**
 * Generates word/numbering.xml for list support.
 */
internal object NumberingWriter {

    fun buildNumberingXml(bulletNumId: Int, numberedNumId: Int): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        appendLine("""<w:numbering xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">""")

        // Abstract numbering for bullets
        appendLine("""  <w:abstractNum w:abstractNumId="0">""")
        appendLine("""    <w:multiLevelType w:val="hybridMultilevel"/>""")
        for (level in 0..8) {
            appendLine("""    <w:lvl w:ilvl="$level">""")
            appendLine("""      <w:start w:val="1"/>""")
            appendLine("""      <w:numFmt w:val="bullet"/>""")
            val bullet = when (level % 3) { 0 -> "\u2022"; 1 -> "\u25CB"; else -> "\u25AA" }
            appendLine("""      <w:lvlText w:val="$bullet"/>""")
            appendLine("""      <w:lvlJc w:val="left"/>""")
            val indent = 720 * (level + 1)
            appendLine("""      <w:pPr><w:ind w:left="$indent" w:hanging="360"/></w:pPr>""")
            appendLine("""    </w:lvl>""")
        }
        appendLine("""  </w:abstractNum>""")

        // Abstract numbering for numbered lists
        appendLine("""  <w:abstractNum w:abstractNumId="1">""")
        appendLine("""    <w:multiLevelType w:val="hybridMultilevel"/>""")
        for (level in 0..8) {
            appendLine("""    <w:lvl w:ilvl="$level">""")
            appendLine("""      <w:start w:val="1"/>""")
            val fmt = when (level % 3) { 0 -> "decimal"; 1 -> "lowerLetter"; else -> "lowerRoman" }
            appendLine("""      <w:numFmt w:val="$fmt"/>""")
            appendLine("""      <w:lvlText w:val="%${level + 1}."/>""")
            appendLine("""      <w:lvlJc w:val="left"/>""")
            val indent = 720 * (level + 1)
            appendLine("""      <w:pPr><w:ind w:left="$indent" w:hanging="360"/></w:pPr>""")
            appendLine("""    </w:lvl>""")
        }
        appendLine("""  </w:abstractNum>""")

        // Num instances
        appendLine("""  <w:num w:numId="$bulletNumId"><w:abstractNumId w:val="0"/></w:num>""")
        appendLine("""  <w:num w:numId="$numberedNumId"><w:abstractNumId w:val="1"/></w:num>""")

        appendLine("""</w:numbering>""")
    }

    const val BULLET_NUM_ID = 1
    const val NUMBERED_NUM_ID = 2
}
