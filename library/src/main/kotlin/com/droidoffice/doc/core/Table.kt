package com.droidoffice.doc.core

import com.droidoffice.core.drawingml.BorderProperties
import com.droidoffice.core.drawingml.BorderStyle

/**
 * A table in a document.
 */
class Table {

    private val _rows = mutableListOf<TableRow>()

    val rows: List<TableRow> get() = _rows

    /** Table borders. */
    var borderTop: BorderProperties = BorderProperties(BorderStyle.THIN)
    var borderBottom: BorderProperties = BorderProperties(BorderStyle.THIN)
    var borderLeft: BorderProperties = BorderProperties(BorderStyle.THIN)
    var borderRight: BorderProperties = BorderProperties(BorderStyle.THIN)
    var borderInsideH: BorderProperties = BorderProperties(BorderStyle.THIN)
    var borderInsideV: BorderProperties = BorderProperties(BorderStyle.THIN)

    fun addRow(): TableRow {
        val row = TableRow()
        _rows.add(row)
        return row
    }

    fun addRow(vararg cellTexts: String): TableRow {
        val row = TableRow()
        for (text in cellTexts) {
            row.addCell(text)
        }
        _rows.add(row)
        return row
    }

    val rowCount: Int get() = _rows.size
}
