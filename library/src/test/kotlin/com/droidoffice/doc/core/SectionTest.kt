package com.droidoffice.doc.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SectionTest {

    @Test
    fun `section has default page setup`() {
        val section = Section()
        assertEquals(PageSize.A4, section.pageSetup.pageSize)
        assertEquals(PageOrientation.PORTRAIT, section.pageSetup.orientation)
    }

    @Test
    fun `section manages headers and footers`() {
        val section = Section()
        val header = section.setHeader(HeaderFooterType.DEFAULT_HEADER)
        header.addParagraph("Header text")
        val footer = section.setFooter(HeaderFooterType.DEFAULT_FOOTER)
        footer.addParagraph("Footer text")

        assertNotNull(section.getHeader())
        assertNotNull(section.getFooter())
        assertEquals("Header text", section.getHeader()!!.text)
        assertEquals("Footer text", section.getFooter()!!.text)
    }
}
