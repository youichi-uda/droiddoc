package com.droidoffice.doc.core

import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle

/**
 * Text run formatting properties.
 */
data class RunStyle(
    var bold: Boolean = false,
    var italic: Boolean = false,
    var underline: UnderlineStyle = UnderlineStyle.NONE,
    var strikethrough: Boolean = false,
    var fontName: String? = null,
    var fontSize: Double? = null,
    var color: OfficeColor? = null,
)
