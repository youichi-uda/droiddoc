package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.ListStyle
import com.droidoffice.doc.core.ListType
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DocxListRoundTripTest {

    @Test
    fun `bullet list round trip`() {
        val doc = Document()
        val p1 = doc.addParagraph("Item 1")
        p1.listStyle = ListStyle(ListType.BULLET, 0)
        val p2 = doc.addParagraph("Item 2")
        p2.listStyle = ListStyle(ListType.BULLET, 0)

        val loaded = roundTrip(doc)
        assertNotNull(loaded.paragraphs[0].listStyle)
        assertEquals(ListType.BULLET, loaded.paragraphs[0].listStyle!!.type)
        assertEquals(0, loaded.paragraphs[0].listStyle!!.level)
    }

    @Test
    fun `numbered list round trip`() {
        val doc = Document()
        val p1 = doc.addParagraph("Step 1")
        p1.listStyle = ListStyle(ListType.NUMBERED, 0)
        val p2 = doc.addParagraph("Sub-step")
        p2.listStyle = ListStyle(ListType.NUMBERED, 1)

        val loaded = roundTrip(doc)
        assertNotNull(loaded.paragraphs[0].listStyle)
        assertEquals(ListType.NUMBERED, loaded.paragraphs[0].listStyle!!.type)
        assertNotNull(loaded.paragraphs[1].listStyle)
        assertEquals(1, loaded.paragraphs[1].listStyle!!.level)
    }

    private fun roundTrip(doc: Document): Document {
        val output = ByteArrayOutputStream()
        doc.save(output)
        return Document.open(ByteArrayInputStream(output.toByteArray()))
    }
}
