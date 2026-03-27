package com.droidoffice.doc.core

import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RunStyleTest {

    @Test
    fun `default RunStyle has no formatting`() {
        val style = RunStyle()
        assertFalse(style.bold)
        assertFalse(style.italic)
        assertEquals(UnderlineStyle.NONE, style.underline)
        assertFalse(style.strikethrough)
        assertEquals(null, style.fontName)
        assertEquals(null, style.fontSize)
        assertEquals(null, style.color)
    }

    @Test
    fun `RunStyle properties are mutable`() {
        val style = RunStyle()
        style.bold = true
        style.italic = true
        style.underline = UnderlineStyle.SINGLE
        style.strikethrough = true
        style.fontName = "Arial"
        style.fontSize = 12.0
        style.color = OfficeColor.Rgb(255, 0, 0)

        assertTrue(style.bold)
        assertTrue(style.italic)
        assertEquals(UnderlineStyle.SINGLE, style.underline)
        assertTrue(style.strikethrough)
        assertEquals("Arial", style.fontName)
        assertEquals(12.0, style.fontSize)
        assertEquals(OfficeColor.Rgb(255, 0, 0), style.color)
    }
}
