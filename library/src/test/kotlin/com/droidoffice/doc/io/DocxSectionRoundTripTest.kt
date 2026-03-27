package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.PageOrientation
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DocxSectionRoundTripTest {

    @Test
    fun `section with header and footer round trip`() {
        val doc = Document()
        doc.addParagraph("Body")
        doc.setHeader().addParagraph("Header")
        doc.setFooter().addParagraph("Footer")
        doc.pageSetup.orientation = PageOrientation.LANDSCAPE

        val loaded = roundTrip(doc)
        assertEquals(PageOrientation.LANDSCAPE, loaded.pageSetup.orientation)
        assertNotNull(loaded.getHeader())
        assertNotNull(loaded.getFooter())
    }

    @Test
    fun `page setup preserved in section`() {
        val doc = Document()
        doc.pageSetup.marginTop = 500
        doc.pageSetup.marginBottom = 500
        doc.addParagraph("Content")

        val loaded = roundTrip(doc)
        assertEquals(500, loaded.pageSetup.marginTop)
        assertEquals(500, loaded.pageSetup.marginBottom)
    }

    private fun roundTrip(doc: Document): Document {
        val output = ByteArrayOutputStream()
        doc.save(output)
        return Document.open(ByteArrayInputStream(output.toByteArray()))
    }
}
