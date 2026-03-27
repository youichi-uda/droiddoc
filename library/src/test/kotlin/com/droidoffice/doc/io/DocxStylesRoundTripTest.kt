package com.droidoffice.doc.io

import com.droidoffice.doc.core.BuiltInStyle
import com.droidoffice.doc.core.Document
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class DocxStylesRoundTripTest {

    @Test
    fun `heading styles round trip`() {
        val doc = Document()
        val h1 = doc.addParagraph("Heading 1")
        h1.styleId = BuiltInStyle.HEADING1.styleId
        val h2 = doc.addParagraph("Heading 2")
        h2.styleId = BuiltInStyle.HEADING2.styleId
        doc.addParagraph("Normal text")

        val loaded = roundTrip(doc)
        assertEquals("Heading1", loaded.paragraphs[0].styleId)
        assertEquals("Heading2", loaded.paragraphs[1].styleId)
        assertEquals(null, loaded.paragraphs[2].styleId)
    }

    @Test
    fun `title and subtitle styles round trip`() {
        val doc = Document()
        doc.addParagraph("My Title").styleId = BuiltInStyle.TITLE.styleId
        doc.addParagraph("My Subtitle").styleId = BuiltInStyle.SUBTITLE.styleId

        val loaded = roundTrip(doc)
        assertEquals("Title", loaded.paragraphs[0].styleId)
        assertEquals("Subtitle", loaded.paragraphs[1].styleId)
    }

    private fun roundTrip(doc: Document): Document {
        val output = ByteArrayOutputStream()
        doc.save(output)
        return Document.open(ByteArrayInputStream(output.toByteArray()))
    }
}
