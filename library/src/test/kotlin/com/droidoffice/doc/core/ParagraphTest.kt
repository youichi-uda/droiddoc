package com.droidoffice.doc.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParagraphTest {

    @Test
    fun `addRun creates run with text`() {
        val para = Paragraph()
        para.addRun("Hello")
        para.addRun(", ")
        para.addRun("World!")
        assertEquals(3, para.runs.size)
        assertEquals("Hello, World!", para.text)
    }

    @Test
    fun `addRun with style block`() {
        val para = Paragraph()
        val run = para.addRun("Bold text") {
            bold = true
            fontSize = 14.0
        }
        assertEquals("Bold text", run.text)
        assertEquals(true, run.style.bold)
        assertEquals(14.0, run.style.fontSize)
    }
}
