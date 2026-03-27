package com.droidoffice.doc.io

import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.ParagraphAlignment
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DocxStyleRoundTripTest {

    @Test
    fun `run style properties preserved after round trip`() {
        val doc = Document()
        val para = doc.addParagraph()
        para.alignment = ParagraphAlignment.CENTER

        val run = para.addRun("Styled text") {
            bold = true
            italic = true
            underline = UnderlineStyle.SINGLE
            fontName = "Arial"
            fontSize = 14.0
            color = OfficeColor.Rgb(255, 0, 0)
        }

        val output = ByteArrayOutputStream()
        doc.save(output)

        val loaded = Document.open(ByteArrayInputStream(output.toByteArray()))
        assertEquals(1, loaded.paragraphCount)
        val loadedPara = loaded.paragraphs[0]
        assertEquals(ParagraphAlignment.CENTER, loadedPara.alignment)

        val loadedRun = loadedPara.runs[0]
        assertEquals("Styled text", loadedRun.text)
        assertTrue(loadedRun.style.bold)
        assertTrue(loadedRun.style.italic)
        assertEquals(UnderlineStyle.SINGLE, loadedRun.style.underline)
        assertEquals("Arial", loadedRun.style.fontName)
        assertEquals(14.0, loadedRun.style.fontSize)
        assertEquals(OfficeColor.Rgb(255, 0, 0), loadedRun.style.color)
    }
}
