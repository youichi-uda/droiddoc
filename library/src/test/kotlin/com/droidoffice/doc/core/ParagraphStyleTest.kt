package com.droidoffice.doc.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParagraphStyleTest {

    @Test
    fun `default paragraph style has no indentation`() {
        val style = ParagraphStyle()
        assertEquals(ParagraphAlignment.LEFT, style.alignment)
        assertNull(style.indentLeft)
        assertNull(style.indentRight)
        assertNull(style.indentFirstLine)
        assertNull(style.lineSpacing)
        assertNull(style.spaceBefore)
        assertNull(style.spaceAfter)
    }

    @Test
    fun `paragraph style accepts indent values`() {
        val style = ParagraphStyle()
        style.indentLeft = 720
        style.indentRight = 360
        style.indentFirstLine = 720
        assertEquals(720, style.indentLeft)
        assertEquals(360, style.indentRight)
        assertEquals(720, style.indentFirstLine)
    }

    @Test
    fun `paragraph style accepts spacing values`() {
        val style = ParagraphStyle()
        style.spaceBefore = 240
        style.spaceAfter = 120
        style.lineSpacing = 360
        style.lineSpacingRule = LineSpacingRule.EXACT
        assertEquals(240, style.spaceBefore)
        assertEquals(120, style.spaceAfter)
        assertEquals(360, style.lineSpacing)
        assertEquals(LineSpacingRule.EXACT, style.lineSpacingRule)
    }
}
