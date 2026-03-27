package com.droidoffice.doc.convert

import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.doc.core.BuiltInStyle
import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.ParagraphAlignment
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class HtmlConverterTest {

    @Test
    fun `basic paragraph to HTML`() {
        val doc = Document()
        doc.addParagraph("Hello, World!")
        val html = HtmlConverter.convert(doc, "Test")
        assertTrue(html.contains("<p>Hello, World!</p>"))
        assertTrue(html.contains("<title>Test</title>"))
    }

    @Test
    fun `heading styles to HTML tags`() {
        val doc = Document()
        doc.addParagraph("Title").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("Subtitle").styleId = BuiltInStyle.HEADING2.styleId
        val html = HtmlConverter.convert(doc)
        assertTrue(html.contains("<h1>Title</h1>"))
        assertTrue(html.contains("<h2>Subtitle</h2>"))
    }

    @Test
    fun `bold and italic in HTML`() {
        val doc = Document()
        val para = doc.addParagraph()
        para.addRun("Bold text") { bold = true }
        val html = HtmlConverter.convert(doc)
        assertTrue(html.contains("font-weight:bold"))
        assertTrue(html.contains("Bold text"))
    }

    @Test
    fun `XSS escaping in HTML`() {
        val doc = Document()
        doc.addParagraph("<script>alert('xss')</script>")
        val html = HtmlConverter.convert(doc)
        assertTrue(!html.contains("<script>"))
        assertTrue(html.contains("&lt;script&gt;"))
    }

    @Test
    fun `Japanese text in HTML`() {
        val doc = Document()
        doc.addParagraph("日本語テスト")
        val html = HtmlConverter.convert(doc, "テスト")
        assertTrue(html.contains("日本語テスト"))
        assertTrue(html.contains("<meta charset=\"UTF-8\">"))
    }
}
