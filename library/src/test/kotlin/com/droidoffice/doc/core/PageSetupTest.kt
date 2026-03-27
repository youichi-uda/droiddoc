package com.droidoffice.doc.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PageSetupTest {

    @Test
    fun `default page setup is A4 portrait`() {
        val ps = PageSetup()
        assertEquals(PageSize.A4, ps.pageSize)
        assertEquals(PageOrientation.PORTRAIT, ps.orientation)
        assertEquals(11906, ps.effectiveWidth)
        assertEquals(16838, ps.effectiveHeight)
    }

    @Test
    fun `landscape orientation swaps dimensions`() {
        val ps = PageSetup(orientation = PageOrientation.LANDSCAPE)
        assertEquals(16838, ps.effectiveWidth)
        assertEquals(11906, ps.effectiveHeight)
    }

    @Test
    fun `custom margins`() {
        val ps = PageSetup(
            marginTop = 720,
            marginBottom = 720,
            marginLeft = 1080,
            marginRight = 1080
        )
        assertEquals(720, ps.marginTop)
        assertEquals(720, ps.marginBottom)
        assertEquals(1080, ps.marginLeft)
        assertEquals(1080, ps.marginRight)
    }
}
