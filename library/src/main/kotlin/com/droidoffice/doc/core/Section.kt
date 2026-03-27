package com.droidoffice.doc.core

/**
 * A document section with its own page setup and header/footer.
 */
class Section {
    val pageSetup = PageSetup()

    private val _headers = mutableMapOf<HeaderFooterType, HeaderFooter>()
    private val _footers = mutableMapOf<HeaderFooterType, HeaderFooter>()

    val headers: Map<HeaderFooterType, HeaderFooter> get() = _headers
    val footers: Map<HeaderFooterType, HeaderFooter> get() = _footers

    fun setHeader(type: HeaderFooterType = HeaderFooterType.DEFAULT_HEADER): HeaderFooter {
        val hf = HeaderFooter(type)
        _headers[type] = hf
        return hf
    }

    fun setFooter(type: HeaderFooterType = HeaderFooterType.DEFAULT_FOOTER): HeaderFooter {
        val hf = HeaderFooter(type)
        _footers[type] = hf
        return hf
    }

    fun getHeader(type: HeaderFooterType = HeaderFooterType.DEFAULT_HEADER): HeaderFooter? = _headers[type]
    fun getFooter(type: HeaderFooterType = HeaderFooterType.DEFAULT_FOOTER): HeaderFooter? = _footers[type]
}
