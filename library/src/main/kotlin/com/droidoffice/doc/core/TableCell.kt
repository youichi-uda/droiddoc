package com.droidoffice.doc.core

import com.droidoffice.core.drawingml.BorderProperties
import com.droidoffice.core.drawingml.OfficeColor

/**
 * A cell in a table row.
 */
class TableCell {

    private val _paragraphs = mutableListOf<Paragraph>()

    val paragraphs: List<Paragraph> get() = _paragraphs

    /** Cell width in twips. Null = auto. */
    var width: Int? = null

    /** Vertical merge: "restart" to start a merge, "continue" to continue. Null = no merge. */
    var verticalMerge: String? = null

    /** Number of columns this cell spans (gridSpan). Default = 1. */
    var gridSpan: Int = 1

    /** Cell shading/background color. */
    var shadingColor: OfficeColor? = null

    fun addParagraph(text: String = ""): Paragraph {
        val para = Paragraph()
        if (text.isNotEmpty()) para.addRun(text)
        _paragraphs.add(para)
        return para
    }

    val text: String get() = _paragraphs.joinToString("\n") { it.text }
}
