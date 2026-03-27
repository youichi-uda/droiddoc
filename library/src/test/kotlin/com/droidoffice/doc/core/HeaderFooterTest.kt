package com.droidoffice.doc.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HeaderFooterTest {

    @Test
    fun `create header with text`() {
        val doc = Document()
        val header = doc.setHeader()
        header.addParagraph("Page Header")
        assertEquals("Page Header", header.text)
        assertNotNull(doc.getHeader())
    }

    @Test
    fun `create footer with text`() {
        val doc = Document()
        val footer = doc.setFooter()
        footer.addParagraph("Page Footer")
        assertEquals("Page Footer", footer.text)
        assertNotNull(doc.getFooter())
    }
}
