package com.droidoffice.doc.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DocumentTest {

    @Test
    fun `addParagraph creates paragraph with text`() {
        val doc = Document()
        val para = doc.addParagraph("Hello, World!")
        assertEquals(1, doc.paragraphCount)
        assertEquals("Hello, World!", para.text)
        assertEquals("Hello, World!", doc.text)
    }

    @Test
    fun `multiple paragraphs joined with newline`() {
        val doc = Document()
        doc.addParagraph("First")
        doc.addParagraph("Second")
        doc.addParagraph("Third")
        assertEquals(3, doc.paragraphCount)
        assertEquals("First\nSecond\nThird", doc.text)
    }

    @Test
    fun `insertParagraph inserts at index`() {
        val doc = Document()
        doc.addParagraph("First")
        doc.addParagraph("Third")
        doc.insertParagraph(1, "Second")
        assertEquals(3, doc.paragraphCount)
        assertEquals("First\nSecond\nThird", doc.text)
    }

    @Test
    fun `removeParagraph removes at index`() {
        val doc = Document()
        doc.addParagraph("Keep")
        doc.addParagraph("Remove")
        doc.addParagraph("Keep too")
        doc.removeParagraph(1)
        assertEquals(2, doc.paragraphCount)
        assertEquals("Keep\nKeep too", doc.text)
    }
}
