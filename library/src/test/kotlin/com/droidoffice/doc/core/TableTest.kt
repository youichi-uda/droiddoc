package com.droidoffice.doc.core

import com.droidoffice.core.drawingml.OfficeColor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TableTest {

    @Test
    fun `create table with rows and cells`() {
        val table = Table()
        val row = table.addRow("Cell 1", "Cell 2", "Cell 3")
        assertEquals(1, table.rowCount)
        assertEquals(3, row.cellCount)
        assertEquals("Cell 1", row.cells[0].text)
        assertEquals("Cell 2", row.cells[1].text)
        assertEquals("Cell 3", row.cells[2].text)
    }

    @Test
    fun `add multiple rows`() {
        val table = Table()
        table.addRow("A", "B")
        table.addRow("C", "D")
        assertEquals(2, table.rowCount)
        assertEquals("A", table.rows[0].cells[0].text)
        assertEquals("D", table.rows[1].cells[1].text)
    }

    @Test
    fun `cell with grid span`() {
        val table = Table()
        val row = table.addRow()
        val cell = row.addCell("Merged")
        cell.gridSpan = 3
        assertEquals(3, cell.gridSpan)
    }

    @Test
    fun `cell shading color`() {
        val table = Table()
        val row = table.addRow()
        val cell = row.addCell("Shaded")
        cell.shadingColor = OfficeColor.Rgb(200, 200, 255)
        assertEquals(OfficeColor.Rgb(200, 200, 255), cell.shadingColor)
    }
}
