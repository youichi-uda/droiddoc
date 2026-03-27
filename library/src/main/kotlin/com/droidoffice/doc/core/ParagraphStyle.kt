package com.droidoffice.doc.core

/**
 * Paragraph-level formatting properties.
 */
data class ParagraphStyle(
    var alignment: ParagraphAlignment = ParagraphAlignment.LEFT,
    /** Left indent in twips (1/1440 inch). */
    var indentLeft: Int? = null,
    /** Right indent in twips. */
    var indentRight: Int? = null,
    /** First line indent in twips. Negative = hanging indent. */
    var indentFirstLine: Int? = null,
    /** Hanging indent in twips. */
    var indentHanging: Int? = null,
    /** Line spacing value. Interpretation depends on lineSpacingRule. */
    var lineSpacing: Int? = null,
    /** Line spacing rule: "auto" (240ths of a line), "exact" (twips), "atLeast" (twips). */
    var lineSpacingRule: LineSpacingRule = LineSpacingRule.AUTO,
    /** Space before paragraph in twips. */
    var spaceBefore: Int? = null,
    /** Space after paragraph in twips. */
    var spaceAfter: Int? = null,
)

enum class LineSpacingRule {
    AUTO,
    EXACT,
    AT_LEAST;

    internal fun toOoxml(): String = when (this) {
        AUTO -> "auto"
        EXACT -> "exact"
        AT_LEAST -> "atLeast"
    }

    companion object {
        internal fun fromOoxml(value: String): LineSpacingRule = when (value) {
            "auto" -> AUTO
            "exact" -> EXACT
            "atLeast" -> AT_LEAST
            else -> AUTO
        }
    }
}
