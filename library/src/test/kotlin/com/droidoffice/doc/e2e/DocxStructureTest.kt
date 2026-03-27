package com.droidoffice.doc.e2e

import com.droidoffice.core.ooxml.OoxmlPackage
import com.droidoffice.doc.core.BuiltInStyle
import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.ListStyle
import com.droidoffice.doc.core.ListType
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DocxStructureTest {

    @Test
    fun `basic docx has required OOXML parts`() {
        val doc = Document()
        doc.addParagraph("Test")

        val output = ByteArrayOutputStream()
        doc.save(output)

        val pkg = OoxmlPackage.open(ByteArrayInputStream(output.toByteArray()))
        assertNotNull(pkg.getPart("[Content_Types].xml"), "Missing [Content_Types].xml")
        assertNotNull(pkg.getPart("_rels/.rels"), "Missing _rels/.rels")
        assertNotNull(pkg.getPart("word/document.xml"), "Missing word/document.xml")
        assertNotNull(pkg.getPart("word/_rels/document.xml.rels"), "Missing document.xml.rels")
        assertNotNull(pkg.getPart("word/styles.xml"), "Missing word/styles.xml")

        // Verify content types
        val ct = String(pkg.getPart("[Content_Types].xml")!!)
        assertTrue(ct.contains("wordprocessingml.document.main"))
        assertTrue(ct.contains("wordprocessingml.styles"))
    }

    @Test
    fun `docx with features has all required parts`() {
        val doc = Document()
        doc.addParagraph("Heading").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("List item").listStyle = ListStyle(ListType.BULLET, 0)
        doc.setHeader().addParagraph("Header")
        val table = doc.addTable()
        table.addRow("A", "B")

        val output = ByteArrayOutputStream()
        doc.save(output)

        val pkg = OoxmlPackage.open(ByteArrayInputStream(output.toByteArray()))

        // Verify numbering part exists for lists
        assertNotNull(pkg.getPart("word/numbering.xml"), "Missing numbering.xml")

        // Verify header part exists
        assertTrue(pkg.partNames().any { it.contains("header") }, "Missing header part")

        // Content types should include numbering and header
        val ct = String(pkg.getPart("[Content_Types].xml")!!)
        assertTrue(ct.contains("numbering"))
        assertTrue(ct.contains("header"))
    }
}
