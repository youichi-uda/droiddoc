package com.droidoffice.doc.io

import com.droidoffice.doc.core.Document
import com.droidoffice.doc.drawing.ImageFormat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DocxImageRoundTripTest {

    @Test
    fun `document with picture creates valid docx`() {
        val doc = Document()
        doc.addParagraph("Document with image")
        val fakePng = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47) // PNG magic bytes
        doc.addPicture(fakePng, ImageFormat.PNG, 914400, 914400)

        val output = ByteArrayOutputStream()
        doc.save(output)
        val bytes = output.toByteArray()
        assertTrue(bytes.isNotEmpty())
        // Verify it's a valid ZIP (PK magic)
        assertEquals(0x50, bytes[0].toInt() and 0xFF)
        assertEquals(0x4B, bytes[1].toInt() and 0xFF)
    }

    @Test
    fun `image data preserved in package`() {
        val doc = Document()
        val imageData = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        doc.addPicture(imageData, ImageFormat.JPEG, 914400, 457200)

        val output = ByteArrayOutputStream()
        doc.save(output)

        // Open the package and verify the image part exists
        val pkg = com.droidoffice.core.ooxml.OoxmlPackage.open(ByteArrayInputStream(output.toByteArray()))
        val imagePart = pkg.getPart("word/media/image1.jpeg")
        assertTrue(imagePart != null)
        assertTrue(imagePart.contentEquals(imageData))
    }
}
