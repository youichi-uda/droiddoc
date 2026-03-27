package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DocxHeaderFooterRoundTripTest {

    @Test
    fun `header round trip`() {
        val doc = Document()
        doc.addParagraph("Body text")
        val header = doc.setHeader()
        header.addParagraph("My Header")

        val loaded = roundTrip(doc)
        assertNotNull(loaded.getHeader())
        assertEquals("My Header", loaded.getHeader()!!.text)
    }

    @Test
    fun `footer round trip`() {
        val doc = Document()
        doc.addParagraph("Body text")
        val footer = doc.setFooter()
        footer.addParagraph("Page 1")

        val loaded = roundTrip(doc)
        assertNotNull(loaded.getFooter())
        assertEquals("Page 1", loaded.getFooter()!!.text)
    }

    private fun roundTrip(doc: Document): Document {
        val output = ByteArrayOutputStream()
        doc.save(output)
        return Document.open(ByteArrayInputStream(output.toByteArray()))
    }
}
