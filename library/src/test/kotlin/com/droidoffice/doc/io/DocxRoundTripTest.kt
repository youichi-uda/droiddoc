package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class DocxRoundTripTest {

    @Test
    fun `basic text round trip`() {
        val doc = Document()
        doc.addParagraph("Hello, DroidDoc!")
        doc.addParagraph("Second paragraph")

        val output = ByteArrayOutputStream()
        doc.save(output)

        val loaded = Document.open(ByteArrayInputStream(output.toByteArray()))
        assertEquals(2, loaded.paragraphCount)
        assertEquals("Hello, DroidDoc!", loaded.paragraphs[0].text)
        assertEquals("Second paragraph", loaded.paragraphs[1].text)
    }

    @Test
    fun `empty document round trip`() {
        val doc = Document()
        doc.addParagraph("")

        val output = ByteArrayOutputStream()
        doc.save(output)

        val loaded = Document.open(ByteArrayInputStream(output.toByteArray()))
        assertEquals(1, loaded.paragraphCount)
    }

    @Test
    fun `Japanese text round trip`() {
        val doc = Document()
        doc.addParagraph("日本語テスト")
        doc.addParagraph("こんにちは世界！")
        doc.addParagraph("漢字・ひらがな・カタカナ")

        val output = ByteArrayOutputStream()
        doc.save(output)

        val loaded = Document.open(ByteArrayInputStream(output.toByteArray()))
        assertEquals(3, loaded.paragraphCount)
        assertEquals("日本語テスト", loaded.paragraphs[0].text)
        assertEquals("こんにちは世界！", loaded.paragraphs[1].text)
        assertEquals("漢字・ひらがな・カタカナ", loaded.paragraphs[2].text)
    }
}
