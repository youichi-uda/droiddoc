package com.droidoffice.doc.e2e

import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import com.droidoffice.doc.convert.HtmlConverter
import com.droidoffice.doc.convert.TextConverter
import com.droidoffice.doc.core.*
import com.droidoffice.doc.drawing.ImageFormat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FullWorkflowTest {

    @Test
    fun `complete document workflow`() {
        // Create
        val doc = Document()
        doc.pageSetup.orientation = PageOrientation.PORTRAIT
        doc.pageSetup.marginTop = 1000

        doc.setHeader().addParagraph("Header Text")
        doc.setFooter().addParagraph("Footer Text")

        doc.addParagraph("Title").styleId = BuiltInStyle.TITLE.styleId
        val styled = doc.addParagraph()
        styled.addRun("Bold ") { bold = true }
        styled.addRun("Colored") { color = OfficeColor.Rgb(0, 128, 0) }
        styled.alignment = ParagraphAlignment.CENTER

        val table = doc.addTable()
        table.addRow("A", "B")
        table.addRow("1", "2")

        doc.addParagraph("Bullet").listStyle = ListStyle(ListType.BULLET, 0)

        val para = doc.addParagraph()
        para.addRun("Visit ")
        para.addHyperlink("https://example.com", "Example")

        val fakePng = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)
        doc.addPicture(fakePng, ImageFormat.PNG, 914400, 457200)

        // Save
        val output = ByteArrayOutputStream()
        doc.save(output)
        assertTrue(output.size() > 0)

        // Read back
        val loaded = Document.open(ByteArrayInputStream(output.toByteArray()))
        assertTrue(loaded.paragraphCount >= 4)
        assertEquals(1, loaded.tables.size)
        assertNotNull(loaded.getHeader())
        assertNotNull(loaded.getFooter())
        assertEquals(PageOrientation.PORTRAIT, loaded.pageSetup.orientation)

        // Convert
        val html = HtmlConverter.convert(loaded, "Test")
        assertTrue(html.contains("<h1>"))
        assertTrue(html.contains("<table"))

        val text = TextConverter.convert(loaded)
        assertTrue(text.contains("Title"))
    }

    @Test
    fun `password-protected full workflow`() {
        val doc = Document()
        doc.addParagraph("Secret heading").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("Confidential body text")
        val table = doc.addTable()
        table.addRow("Key", "Value")
        table.addRow("Password", "****")

        val output = ByteArrayOutputStream()
        doc.save(output, "p@ssw0rd")

        val loaded = Document.open(ByteArrayInputStream(output.toByteArray()), "p@ssw0rd")
        assertTrue(loaded.paragraphCount >= 2)
        assertEquals("Secret heading", loaded.paragraphs[0].text)
        assertEquals(1, loaded.tables.size)
    }
}
