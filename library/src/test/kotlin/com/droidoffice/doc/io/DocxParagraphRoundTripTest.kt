package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.LineSpacingRule
import com.droidoffice.doc.core.ParagraphAlignment
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class DocxParagraphRoundTripTest {

    @Test
    fun `paragraph alignment round trip`() {
        val doc = Document()
        doc.addParagraph("Left aligned")
        doc.addParagraph("Center aligned").apply { alignment = ParagraphAlignment.CENTER }
        doc.addParagraph("Right aligned").apply { alignment = ParagraphAlignment.RIGHT }
        doc.addParagraph("Justified").apply { alignment = ParagraphAlignment.JUSTIFY }

        val loaded = roundTrip(doc)
        assertEquals(ParagraphAlignment.LEFT, loaded.paragraphs[0].alignment)
        assertEquals(ParagraphAlignment.CENTER, loaded.paragraphs[1].alignment)
        assertEquals(ParagraphAlignment.RIGHT, loaded.paragraphs[2].alignment)
        assertEquals(ParagraphAlignment.JUSTIFY, loaded.paragraphs[3].alignment)
    }

    @Test
    fun `paragraph indentation round trip`() {
        val doc = Document()
        val para = doc.addParagraph("Indented paragraph")
        para.paragraphStyle.indentLeft = 720
        para.paragraphStyle.indentRight = 360
        para.paragraphStyle.indentFirstLine = 720

        val loaded = roundTrip(doc)
        val ps = loaded.paragraphs[0].paragraphStyle
        assertEquals(720, ps.indentLeft)
        assertEquals(360, ps.indentRight)
        assertEquals(720, ps.indentFirstLine)
    }

    @Test
    fun `paragraph spacing round trip`() {
        val doc = Document()
        val para = doc.addParagraph("Spaced paragraph")
        para.paragraphStyle.spaceBefore = 240
        para.paragraphStyle.spaceAfter = 120
        para.paragraphStyle.lineSpacing = 360
        para.paragraphStyle.lineSpacingRule = LineSpacingRule.EXACT

        val loaded = roundTrip(doc)
        val ps = loaded.paragraphs[0].paragraphStyle
        assertEquals(240, ps.spaceBefore)
        assertEquals(120, ps.spaceAfter)
        assertEquals(360, ps.lineSpacing)
        assertEquals(LineSpacingRule.EXACT, ps.lineSpacingRule)
    }

    @Test
    fun `multiple runs in paragraph round trip`() {
        val doc = Document()
        val para = doc.addParagraph()
        para.addRun("Normal text ")
        para.addRun("Bold text ") { bold = true }
        para.addRun("Italic text") { italic = true }

        val loaded = roundTrip(doc)
        val loadedPara = loaded.paragraphs[0]
        assertEquals(3, loadedPara.runs.size)
        assertEquals("Normal text ", loadedPara.runs[0].text)
        assertEquals("Bold text ", loadedPara.runs[1].text)
        assertEquals(true, loadedPara.runs[1].style.bold)
        assertEquals("Italic text", loadedPara.runs[2].text)
        assertEquals(true, loadedPara.runs[2].style.italic)
    }

    private fun roundTrip(doc: Document): Document {
        val output = ByteArrayOutputStream()
        doc.save(output)
        return Document.open(ByteArrayInputStream(output.toByteArray()))
    }
}
