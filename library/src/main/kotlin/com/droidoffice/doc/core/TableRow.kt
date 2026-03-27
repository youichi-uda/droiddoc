package com.droidoffice.doc.core

/**
 * A row in a table.
 */
class TableRow {

    private val _cells = mutableListOf<TableCell>()

    val cells: List<TableCell> get() = _cells

    /** Row height in twips. Null = auto. */
    var height: Int? = null

    fun addCell(text: String = ""): TableCell {
        val cell = TableCell()
        if (text.isNotEmpty()) cell.addParagraph(text)
        _cells.add(cell)
        return cell
    }

    val cellCount: Int get() = _cells.size
}
