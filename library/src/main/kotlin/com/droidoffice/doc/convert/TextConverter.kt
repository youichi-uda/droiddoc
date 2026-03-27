package com.droidoffice.doc.convert

import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.Paragraph
import com.droidoffice.doc.core.Table

/**
 * Extracts plain text from a Document.
 */
object TextConverter {

    fun convert(document: Document): String = buildString {
        for ((i, element) in document.bodyElements.withIndex()) {
            if (i > 0) appendLine()
            when (element) {
                is Paragraph -> append(element.text)
                is Table -> convertTable(this, element)
            }
        }
    }

    private fun convertTable(sb: StringBuilder, table: Table) {
        for ((i, row) in table.rows.withIndex()) {
            if (i > 0) sb.appendLine()
            sb.append(row.cells.joinToString("\t") { it.text })
        }
    }
}
