package com.droidoffice.doc.core

/**
 * A header or footer for a document section.
 */
class HeaderFooter(val type: HeaderFooterType) {

    private val _paragraphs = mutableListOf<Paragraph>()

    val paragraphs: List<Paragraph> get() = _paragraphs

    fun addParagraph(text: String = ""): Paragraph {
        val para = Paragraph()
        if (text.isNotEmpty()) para.addRun(text)
        _paragraphs.add(para)
        return para
    }

    val text: String get() = _paragraphs.joinToString("\n") { it.text }
}

enum class HeaderFooterType {
    DEFAULT_HEADER,
    DEFAULT_FOOTER,
    FIRST_PAGE_HEADER,
    FIRST_PAGE_FOOTER,
    EVEN_PAGE_HEADER,
    EVEN_PAGE_FOOTER,
}
