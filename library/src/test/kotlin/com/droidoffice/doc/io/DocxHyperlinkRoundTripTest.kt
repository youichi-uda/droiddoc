package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertTrue

class DocxHyperlinkRoundTripTest {

    @Test
    fun `document with hyperlink creates valid docx`() {
        val doc = Document()
        val para = doc.addParagraph()
        para.addRun("Visit ")
        para.addHyperlink("https://example.com", "Example")

        val output = ByteArrayOutputStream()
        doc.save(output)
        val bytes = output.toByteArray()
        assertTrue(bytes.isNotEmpty())
    }

    @Test
    fun `hyperlink XML contains correct relationship`() {
        val doc = Document()
        val para = doc.addParagraph()
        para.addHyperlink("https://droidoffice.abyo.net", "DroidOffice")

        val output = ByteArrayOutputStream()
        doc.save(output)

        val pkg = com.droidoffice.core.ooxml.OoxmlPackage.open(java.io.ByteArrayInputStream(output.toByteArray()))
        val rels = String(pkg.getPart("word/_rels/document.xml.rels")!!)
        assertTrue(rels.contains("https://droidoffice.abyo.net"))
        assertTrue(rels.contains("hyperlink"))
    }
}
