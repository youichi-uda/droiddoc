package com.droidoffice.doc.io

import com.droidoffice.core.exception.PasswordException
import com.droidoffice.doc.core.Document
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class DocxPasswordTest {

    @Test
    fun `save and open with password`() {
        val doc = Document()
        doc.addParagraph("Secret content")

        val output = ByteArrayOutputStream()
        doc.save(output, "mypassword")

        val loaded = Document.open(ByteArrayInputStream(output.toByteArray()), "mypassword")
        assertEquals("Secret content", loaded.paragraphs[0].text)
    }

    @Test
    fun `open encrypted file without password throws`() {
        val doc = Document()
        doc.addParagraph("Secret")

        val output = ByteArrayOutputStream()
        doc.save(output, "password123")

        assertThrows<PasswordException> {
            Document.open(ByteArrayInputStream(output.toByteArray()))
        }
    }

    @Test
    fun `wrong password throws`() {
        val doc = Document()
        doc.addParagraph("Secret")

        val output = ByteArrayOutputStream()
        doc.save(output, "correct")

        assertThrows<PasswordException> {
            Document.open(ByteArrayInputStream(output.toByteArray()), "wrong")
        }
    }
}
