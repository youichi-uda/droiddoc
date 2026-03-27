package com.droidoffice.doc.core

import com.droidoffice.doc.drawing.Picture
import com.droidoffice.doc.io.DocxReader
import com.droidoffice.doc.io.DocxWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

/**
 * The main entry point for working with Word documents.
 */
class Document {

    private val _paragraphs = mutableListOf<Paragraph>()

    val paragraphs: List<Paragraph> get() = _paragraphs

    /** All body-level elements in document order (paragraphs, tables, etc.). */
    internal val bodyElements = mutableListOf<Any>()

    /** Page setup for the document (section properties). */
    val pageSetup = PageSetup()

    private val _tables = mutableListOf<Table>()
    val tables: List<Table> get() = _tables

    private val _pictures = mutableListOf<Picture>()
    val pictures: List<Picture> get() = _pictures

    /** Default section (last section in document). */
    val defaultSection = Section()

    fun setHeader(type: HeaderFooterType = HeaderFooterType.DEFAULT_HEADER): HeaderFooter =
        defaultSection.setHeader(type)

    fun setFooter(type: HeaderFooterType = HeaderFooterType.DEFAULT_FOOTER): HeaderFooter =
        defaultSection.setFooter(type)

    fun getHeader(type: HeaderFooterType = HeaderFooterType.DEFAULT_HEADER): HeaderFooter? =
        defaultSection.getHeader(type)

    fun getFooter(type: HeaderFooterType = HeaderFooterType.DEFAULT_FOOTER): HeaderFooter? =
        defaultSection.getFooter(type)

    fun addParagraph(text: String = ""): Paragraph {
        val para = Paragraph()
        if (text.isNotEmpty()) {
            para.addRun(text)
        }
        _paragraphs.add(para)
        bodyElements.add(para)
        return para
    }

    fun addParagraph(text: String, block: Paragraph.() -> Unit): Paragraph {
        val para = addParagraph(text)
        para.block()
        return para
    }

    fun insertParagraph(index: Int, text: String = ""): Paragraph {
        val para = Paragraph()
        if (text.isNotEmpty()) {
            para.addRun(text)
        }
        _paragraphs.add(index, para)
        bodyElements.add(index, para)
        return para
    }

    fun removeParagraph(index: Int) {
        val para = _paragraphs.removeAt(index)
        bodyElements.remove(para)
    }

    val paragraphCount: Int get() = _paragraphs.size

    /** Get concatenated text of all paragraphs. */
    val text: String get() = _paragraphs.joinToString("\n") { it.text }

    fun addTable(): Table {
        val table = Table()
        _tables.add(table)
        bodyElements.add(table)
        return table
    }

    fun addPicture(data: ByteArray, format: com.droidoffice.doc.drawing.ImageFormat, widthEmu: Long, heightEmu: Long): Picture {
        val pic = Picture(data, format, widthEmu, heightEmu)
        _pictures.add(pic)
        return pic
    }

    // -- I/O --

    fun save(output: OutputStream) {
        DocxWriter.write(this, output)
    }

    fun save(output: OutputStream, password: String) {
        val buffer = java.io.ByteArrayOutputStream()
        DocxWriter.write(this, buffer)
        val encrypted = com.droidoffice.core.ooxml.EncryptedPackage.encrypt(buffer.toByteArray(), password)
        output.write(encrypted)
    }

    suspend fun saveAsync(output: OutputStream) {
        withContext(Dispatchers.IO) { save(output) }
    }

    suspend fun saveAsync(output: OutputStream, password: String) {
        withContext(Dispatchers.IO) { save(output, password) }
    }

    companion object {
        fun open(input: InputStream): Document {
            val bytes = input.readBytes()
            return if (com.droidoffice.core.ooxml.EncryptedPackage.isEncrypted(bytes)) {
                throw com.droidoffice.core.exception.PasswordException(
                    "This file is password-protected. Use open(input, password) instead."
                )
            } else {
                DocxReader.read(java.io.ByteArrayInputStream(bytes))
            }
        }

        fun open(input: InputStream, password: String): Document {
            val bytes = input.readBytes()
            val decrypted = if (com.droidoffice.core.ooxml.EncryptedPackage.isEncrypted(bytes)) {
                com.droidoffice.core.ooxml.EncryptedPackage.decrypt(bytes, password)
            } else {
                bytes
            }
            return DocxReader.read(java.io.ByteArrayInputStream(decrypted))
        }

        suspend fun openAsync(input: InputStream): Document {
            return withContext(Dispatchers.IO) { open(input) }
        }

        suspend fun openAsync(input: InputStream, password: String): Document {
            return withContext(Dispatchers.IO) { open(input, password) }
        }
    }
}
