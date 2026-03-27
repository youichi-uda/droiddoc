package com.droidoffice.doc.convert

import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.Paragraph
import com.droidoffice.doc.core.ParagraphAlignment
import com.droidoffice.doc.core.Run
import com.droidoffice.doc.core.Table

/**
 * Converts a Document to HTML.
 */
object HtmlConverter {

    fun convert(document: Document, title: String = ""): String = buildString {
        appendLine("<!DOCTYPE html>")
        appendLine("<html>")
        appendLine("<head>")
        appendLine("<meta charset=\"UTF-8\">")
        appendLine("<title>${escapeHtml(title)}</title>")
        appendLine("</head>")
        appendLine("<body>")

        for (element in document.bodyElements) {
            when (element) {
                is Paragraph -> convertParagraph(this, element)
                is Table -> convertTable(this, element)
            }
        }

        appendLine("</body>")
        appendLine("</html>")
    }

    private fun convertParagraph(sb: StringBuilder, paragraph: Paragraph) {
        val tag = when (paragraph.styleId) {
            "Heading1" -> "h1"
            "Heading2" -> "h2"
            "Heading3" -> "h3"
            "Heading4" -> "h4"
            "Heading5" -> "h5"
            "Heading6" -> "h6"
            "Title" -> "h1"
            else -> "p"
        }

        val style = buildStyleAttr(paragraph)
        sb.append("<$tag$style>")

        // Hyperlinks
        for (link in paragraph.hyperlinks) {
            sb.append("<a href=\"${escapeHtml(link.url)}\">${escapeHtml(link.text)}</a>")
        }

        for (run in paragraph.runs) {
            convertRun(sb, run)
        }

        sb.appendLine("</$tag>")
    }

    private fun buildStyleAttr(paragraph: Paragraph): String {
        val styles = mutableListOf<String>()
        when (paragraph.alignment) {
            ParagraphAlignment.CENTER -> styles.add("text-align:center")
            ParagraphAlignment.RIGHT -> styles.add("text-align:right")
            ParagraphAlignment.JUSTIFY -> styles.add("text-align:justify")
            else -> {}
        }
        return if (styles.isNotEmpty()) " style=\"${styles.joinToString(";")}\"" else ""
    }

    private fun convertRun(sb: StringBuilder, run: Run) {
        val styles = mutableListOf<String>()
        val s = run.style

        if (s.fontName != null) styles.add("font-family:${escapeHtml(s.fontName!!)}")
        if (s.fontSize != null) styles.add("font-size:${s.fontSize}pt")
        s.color?.let { color ->
            if (color is OfficeColor.Rgb) styles.add("color:#${color.toHex()}")
        }
        if (s.bold) styles.add("font-weight:bold")
        if (s.italic) styles.add("font-style:italic")
        if (s.underline != UnderlineStyle.NONE) styles.add("text-decoration:underline")
        if (s.strikethrough) styles.add("text-decoration:line-through")

        val styleAttr = if (styles.isNotEmpty()) " style=\"${styles.joinToString(";")}\"" else ""
        val needsSpan = styleAttr.isNotEmpty()

        if (needsSpan) sb.append("<span$styleAttr>")
        sb.append(escapeHtml(run.text))
        if (needsSpan) sb.append("</span>")
    }

    private fun convertTable(sb: StringBuilder, table: Table) {
        sb.appendLine("<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">")
        for (row in table.rows) {
            sb.appendLine("<tr>")
            for (cell in row.cells) {
                val colspan = if (cell.gridSpan > 1) " colspan=\"${cell.gridSpan}\"" else ""
                sb.append("<td$colspan>")
                for ((i, para) in cell.paragraphs.withIndex()) {
                    if (i > 0) sb.append("<br>")
                    for (r in para.runs) {
                        sb.append(escapeHtml(r.text))
                    }
                }
                sb.appendLine("</td>")
            }
            sb.appendLine("</tr>")
        }
        sb.appendLine("</table>")
    }

    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}
