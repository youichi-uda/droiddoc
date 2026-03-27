package com.droidoffice.doc.core

/**
 * List type for a paragraph.
 */
enum class ListType {
    BULLET,
    NUMBERED,
}

/**
 * List formatting applied to a paragraph.
 */
data class ListStyle(
    var type: ListType = ListType.BULLET,
    /** List level (0-based). */
    var level: Int = 0,
    /** Abstract numbering ID (internal, set during read). */
    internal var numId: Int = 0,
)
