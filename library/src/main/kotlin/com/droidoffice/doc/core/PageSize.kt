package com.droidoffice.doc.core

/**
 * Standard page sizes. Dimensions in twips (1/1440 inch).
 */
enum class PageSize(val widthTwips: Int, val heightTwips: Int) {
    A4(11906, 16838),
    LETTER(12240, 15840),
    LEGAL(12240, 20160),
    A3(16838, 23811),
    A5(8391, 11906),
    B5(10319, 14571),
    EXECUTIVE(10440, 15120),
    CUSTOM(0, 0);
}
