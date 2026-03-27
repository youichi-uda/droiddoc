package com.droidoffice.doc.core

/**
 * Paragraph text alignment.
 */
enum class ParagraphAlignment {
    LEFT,
    CENTER,
    RIGHT,
    JUSTIFY;

    internal fun toOoxml(): String = when (this) {
        LEFT -> "left"
        CENTER -> "center"
        RIGHT -> "right"
        JUSTIFY -> "both"
    }

    companion object {
        internal fun fromOoxml(value: String): ParagraphAlignment = when (value) {
            "left", "start" -> LEFT
            "center" -> CENTER
            "right", "end" -> RIGHT
            "both", "distribute" -> JUSTIFY
            else -> LEFT
        }
    }
}
