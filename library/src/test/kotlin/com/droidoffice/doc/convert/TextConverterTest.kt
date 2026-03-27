package com.droidoffice.doc.convert

import com.droidoffice.doc.core.Document
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TextConverterTest {

    @Test
    fun `extract text from paragraphs`() {
        val doc = Document()
        doc.addParagraph("First paragraph")
        doc.addParagraph("Second paragraph")
        val text = TextConverter.convert(doc)
        assertEquals("First paragraph\nSecond paragraph", text)
    }

    @Test
    fun `extract text from table`() {
        val doc = Document()
        val table = doc.addTable()
        table.addRow("A1", "B1")
        table.addRow("A2", "B2")
        val text = TextConverter.convert(doc)
        assertTrue(text.contains("A1\tB1"))
        assertTrue(text.contains("A2\tB2"))
    }

    private fun assertTrue(condition: Boolean) {
        kotlin.test.assertTrue(condition)
    }
}
