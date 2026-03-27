package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class DocxTableRoundTripTest {

    @Test
    fun `basic table round trip`() {
        val doc = Document()
        doc.addParagraph("Before table")
        val table = doc.addTable()
        table.addRow("A1", "B1", "C1")
        table.addRow("A2", "B2", "C2")

        val loaded = roundTrip(doc)
        assertEquals(1, loaded.tables.size)
        val t = loaded.tables[0]
        assertEquals(2, t.rowCount)
        assertEquals(3, t.rows[0].cellCount)
        assertEquals("A1", t.rows[0].cells[0].text)
        assertEquals("C2", t.rows[1].cells[2].text)
    }

    @Test
    fun `table with cell properties round trip`() {
        val doc = Document()
        val table = doc.addTable()
        val row = table.addRow()
        val cell = row.addCell("Wide cell")
        cell.width = 5000
        cell.gridSpan = 2

        val loaded = roundTrip(doc)
        val loadedCell = loaded.tables[0].rows[0].cells[0]
        assertEquals(5000, loadedCell.width)
        assertEquals(2, loadedCell.gridSpan)
    }

    @Test
    fun `table with multi-paragraph cells`() {
        val doc = Document()
        val table = doc.addTable()
        val row = table.addRow()
        val cell = row.addCell("Line 1")
        cell.addParagraph("Line 2")

        val loaded = roundTrip(doc)
        val loadedCell = loaded.tables[0].rows[0].cells[0]
        assertEquals(2, loadedCell.paragraphs.size)
        assertEquals("Line 1", loadedCell.paragraphs[0].text)
        assertEquals("Line 2", loadedCell.paragraphs[1].text)
    }

    private fun roundTrip(doc: Document): Document {
        val output = ByteArrayOutputStream()
        doc.save(output)
        return Document.open(ByteArrayInputStream(output.toByteArray()))
    }
}
