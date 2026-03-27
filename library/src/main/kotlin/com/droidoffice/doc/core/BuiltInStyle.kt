package com.droidoffice.doc.core

/**
 * Built-in Word styles that can be applied to paragraphs.
 */
enum class BuiltInStyle(val styleId: String, val styleName: String) {
    NORMAL("Normal", "Normal"),
    HEADING1("Heading1", "heading 1"),
    HEADING2("Heading2", "heading 2"),
    HEADING3("Heading3", "heading 3"),
    HEADING4("Heading4", "heading 4"),
    HEADING5("Heading5", "heading 5"),
    HEADING6("Heading6", "heading 6"),
    TITLE("Title", "Title"),
    SUBTITLE("Subtitle", "Subtitle"),
    QUOTE("Quote", "Quote"),
    LIST_PARAGRAPH("ListParagraph", "List Paragraph"),
    TOC_HEADING("TOCHeading", "TOC Heading"),
}
