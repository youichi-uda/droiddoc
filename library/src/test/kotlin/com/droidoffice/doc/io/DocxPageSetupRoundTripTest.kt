package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.PageOrientation
import com.droidoffice.doc.core.PageSize
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class DocxPageSetupRoundTripTest {

    @Test
    fun `default A4 portrait page setup round trip`() {
        val doc = Document()
        doc.addParagraph("Test")

        val loaded = roundTrip(doc)
        assertEquals(PageSize.A4, loaded.pageSetup.pageSize)
        assertEquals(PageOrientation.PORTRAIT, loaded.pageSetup.orientation)
    }

    @Test
    fun `landscape orientation round trip`() {
        val doc = Document()
        doc.pageSetup.orientation = PageOrientation.LANDSCAPE
        doc.addParagraph("Landscape")

        val loaded = roundTrip(doc)
        assertEquals(PageOrientation.LANDSCAPE, loaded.pageSetup.orientation)
    }

    @Test
    fun `custom margins round trip`() {
        val doc = Document()
        doc.pageSetup.marginTop = 720
        doc.pageSetup.marginBottom = 720
        doc.pageSetup.marginLeft = 1080
        doc.pageSetup.marginRight = 1080
        doc.addParagraph("Custom margins")

        val loaded = roundTrip(doc)
        assertEquals(720, loaded.pageSetup.marginTop)
        assertEquals(720, loaded.pageSetup.marginBottom)
        assertEquals(1080, loaded.pageSetup.marginLeft)
        assertEquals(1080, loaded.pageSetup.marginRight)
    }

    private fun roundTrip(doc: Document): Document {
        val output = ByteArrayOutputStream()
        doc.save(output)
        return Document.open(ByteArrayInputStream(output.toByteArray()))
    }
}
