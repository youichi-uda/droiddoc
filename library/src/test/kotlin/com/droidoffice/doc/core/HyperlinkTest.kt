package com.droidoffice.doc.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HyperlinkTest {

    @Test
    fun `create hyperlink`() {
        val link = Hyperlink("https://example.com", "Example")
        assertEquals("https://example.com", link.url)
        assertEquals("Example", link.text)
    }

    @Test
    fun `paragraph with hyperlink`() {
        val para = Paragraph()
        para.addRun("Visit ")
        val link = para.addHyperlink("https://example.com", "Example")
        assertEquals(1, para.hyperlinks.size)
        assertEquals("Example", link.text)
    }
}
