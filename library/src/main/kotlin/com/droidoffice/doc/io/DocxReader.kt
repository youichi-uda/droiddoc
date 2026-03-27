package com.droidoffice.doc.io

import com.droidoffice.core.exception.InvalidFileException
import com.droidoffice.core.ooxml.OoxmlPackage
import com.droidoffice.core.ooxml.SaxReader
import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import com.droidoffice.doc.core.Document
import com.droidoffice.doc.core.HeaderFooter
import com.droidoffice.doc.core.HeaderFooterType
import com.droidoffice.doc.core.LineSpacingRule
import com.droidoffice.doc.core.ListStyle
import com.droidoffice.doc.core.ListType
import com.droidoffice.doc.core.PageOrientation
import com.droidoffice.doc.core.PageSize
import com.droidoffice.doc.core.Paragraph
import com.droidoffice.doc.core.ParagraphAlignment
import com.droidoffice.doc.core.Run
import com.droidoffice.doc.core.Table
import com.droidoffice.doc.core.TableCell
import com.droidoffice.doc.core.TableRow
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream

/**
 * Reads .docx files (OOXML WordprocessingML) using SAX streaming.
 */
object DocxReader {

    fun read(input: InputStream): Document {
        val pkg = OoxmlPackage.open(input)
        val document = Document()

        // Read relationships to find header/footer targets
        val rels = pkg.getPartAsStream("word/_rels/document.xml.rels")?.let { stream ->
            com.droidoffice.core.ooxml.parseRelationships(stream)
        } ?: emptyList()

        pkg.getPartAsStream("word/document.xml")?.let { stream ->
            readDocumentBody(stream, document)
        } ?: throw InvalidFileException("Missing word/document.xml")

        // Read header/footer parts based on rels collected during document parsing
        for (rel in rels) {
            when {
                rel.type.contains("header") -> {
                    val path = "word/${rel.target}"
                    pkg.getPartAsStream(path)?.let { stream ->
                        val hf = document.setHeader(HeaderFooterType.DEFAULT_HEADER)
                        readHeaderFooter(stream, hf)
                    }
                }
                rel.type.contains("footer") -> {
                    val path = "word/${rel.target}"
                    pkg.getPartAsStream(path)?.let { stream ->
                        val hf = document.setFooter(HeaderFooterType.DEFAULT_FOOTER)
                        readHeaderFooter(stream, hf)
                    }
                }
            }
        }

        return document
    }

    private fun readHeaderFooter(input: InputStream, hf: HeaderFooter) {
        SaxReader.parse(input, HeaderFooterHandler(hf))
    }

    private class HeaderFooterHandler(private val hf: HeaderFooter) : DefaultHandler() {
        private val textBuffer = StringBuilder()
        private var currentParagraph: com.droidoffice.doc.core.Paragraph? = null
        private var inText = false

        override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
            when (localName) {
                "p" -> currentParagraph = hf.addParagraph()
                "t" -> { inText = true; textBuffer.clear() }
            }
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            if (inText) textBuffer.append(ch, start, length)
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            when (localName) {
                "t" -> {
                    inText = false
                    currentParagraph?.addRun(textBuffer.toString())
                }
                "p" -> currentParagraph = null
            }
        }
    }

    private fun readDocumentBody(input: InputStream, document: Document) {
        SaxReader.parse(input, DocumentHandler(document))
    }

    private class DocumentHandler(
        private val document: Document,
    ) : DefaultHandler() {

        private val textBuffer = StringBuilder()
        private var currentParagraph: Paragraph? = null
        private var currentRun: Run? = null

        // Run properties state
        private var inRunProps = false
        private var runBold = false
        private var runItalic = false
        private var runUnderline = UnderlineStyle.NONE
        private var runStrikethrough = false
        private var runFontName: String? = null
        private var runFontSize: Double? = null
        private var runColor: OfficeColor? = null

        // Paragraph properties state
        private var inParaProps = false
        private var paraAlignment: ParagraphAlignment = ParagraphAlignment.LEFT
        private var paraStyleId: String? = null
        private var paraIndentLeft: Int? = null
        private var paraIndentRight: Int? = null
        private var paraIndentFirstLine: Int? = null
        private var paraIndentHanging: Int? = null
        private var paraSpaceBefore: Int? = null
        private var paraSpaceAfter: Int? = null
        private var paraLineSpacing: Int? = null
        private var paraLineSpacingRule: LineSpacingRule = LineSpacingRule.AUTO

        // List properties
        private var paraNumId: Int? = null
        private var paraIlvl: Int = 0

        // Table state
        private var currentTable: Table? = null
        private var currentRow: TableRow? = null
        private var currentCell: TableCell? = null
        private var inTable = false
        private var inTableCell = false
        private var inCellProps = false
        private var cellWidth: Int? = null
        private var cellGridSpan: Int = 1
        private var cellVMerge: String? = null

        // Hyperlink state
        private var inHyperlink = false
        private var hyperlinkRelId: String? = null

        // Section properties state
        private var inSectPr = false

        private var inText = false

        override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
            when (localName) {
                "tbl" -> {
                    val table = document.addTable()
                    currentTable = table
                    inTable = true
                }
                "tr" -> if (inTable) {
                    val row = currentTable!!.addRow()
                    currentRow = row
                }
                "tc" -> if (inTable) {
                    val cell = currentRow!!.addCell()
                    currentCell = cell
                    inTableCell = true
                    cellWidth = null
                    cellGridSpan = 1
                    cellVMerge = null
                }
                "tcPr" -> if (inTableCell) inCellProps = true
                "tcW" -> if (inCellProps) {
                    cellWidth = getAttr(attributes, "w")?.toIntOrNull()
                }
                "gridSpan" -> if (inCellProps) {
                    cellGridSpan = getAttr(attributes, "val")?.toIntOrNull() ?: 1
                }
                "vMerge" -> if (inCellProps) {
                    cellVMerge = getAttr(attributes, "val") ?: "continue"
                }
                "hyperlink" -> {
                    inHyperlink = true
                    hyperlinkRelId = attributes.getValue(NS_R, "id")
                        ?: attributes.getValue("r:id")
                }
                "p" -> {
                    val para = if (inTableCell) {
                        currentCell!!.addParagraph()
                    } else {
                        document.addParagraph()
                    }
                    currentParagraph = para
                    paraAlignment = ParagraphAlignment.LEFT
                    paraStyleId = null
                    paraIndentLeft = null
                    paraIndentRight = null
                    paraIndentFirstLine = null
                    paraIndentHanging = null
                    paraSpaceBefore = null
                    paraSpaceAfter = null
                    paraLineSpacing = null
                    paraLineSpacingRule = LineSpacingRule.AUTO
                    paraNumId = null
                    paraIlvl = 0
                }
                "pPr" -> if (!inSectPr) inParaProps = true
                "jc" -> if (inParaProps) {
                    val value = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                    if (value != null) {
                        paraAlignment = ParagraphAlignment.fromOoxml(value)
                    }
                }
                "pStyle" -> if (inParaProps) {
                    paraStyleId = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                }
                "numId" -> if (inParaProps) {
                    paraNumId = getAttr(attributes, "val")?.toIntOrNull()
                }
                "ilvl" -> if (inParaProps) {
                    paraIlvl = getAttr(attributes, "val")?.toIntOrNull() ?: 0
                }
                "spacing" -> if (inParaProps) {
                    val before = getAttr(attributes, "before")
                    val after = getAttr(attributes, "after")
                    val line = getAttr(attributes, "line")
                    val lineRule = getAttr(attributes, "lineRule")
                    paraSpaceBefore = before?.toIntOrNull()
                    paraSpaceAfter = after?.toIntOrNull()
                    paraLineSpacing = line?.toIntOrNull()
                    if (lineRule != null) paraLineSpacingRule = LineSpacingRule.fromOoxml(lineRule)
                }
                "ind" -> if (inParaProps) {
                    paraIndentLeft = getAttr(attributes, "left")?.toIntOrNull()
                    paraIndentRight = getAttr(attributes, "right")?.toIntOrNull()
                    paraIndentFirstLine = getAttr(attributes, "firstLine")?.toIntOrNull()
                    paraIndentHanging = getAttr(attributes, "hanging")?.toIntOrNull()
                }
                "sectPr" -> {
                    inSectPr = true
                }
                "pgSz" -> if (inSectPr) {
                    val w = getAttr(attributes, "w")?.toIntOrNull()
                    val h = getAttr(attributes, "h")?.toIntOrNull()
                    val orient = getAttr(attributes, "orient")
                    if (orient != null) {
                        document.pageSetup.orientation = PageOrientation.fromOoxml(orient)
                    }
                    if (w != null && h != null) {
                        document.pageSetup.customWidth = w
                        document.pageSetup.customHeight = h
                        // Try to match a standard page size
                        val matchedSize = PageSize.entries
                            .filter { it != PageSize.CUSTOM }
                            .find {
                                (it.widthTwips == w && it.heightTwips == h) ||
                                (it.heightTwips == w && it.widthTwips == h)
                            }
                        if (matchedSize != null) {
                            document.pageSetup.pageSize = matchedSize
                        } else {
                            document.pageSetup.pageSize = PageSize.CUSTOM
                        }
                    }
                }
                "pgMar" -> if (inSectPr) {
                    getAttr(attributes, "top")?.toIntOrNull()?.let { document.pageSetup.marginTop = it }
                    getAttr(attributes, "bottom")?.toIntOrNull()?.let { document.pageSetup.marginBottom = it }
                    getAttr(attributes, "left")?.toIntOrNull()?.let { document.pageSetup.marginLeft = it }
                    getAttr(attributes, "right")?.toIntOrNull()?.let { document.pageSetup.marginRight = it }
                    getAttr(attributes, "header")?.toIntOrNull()?.let { document.pageSetup.marginHeader = it }
                    getAttr(attributes, "footer")?.toIntOrNull()?.let { document.pageSetup.marginFooter = it }
                }
                "r" -> {
                    currentRun = null
                    resetRunProps()
                }
                "rPr" -> inRunProps = true
                "b" -> if (inRunProps) {
                    val value = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                    runBold = value == null || value != "0" && value != "false"
                }
                "i" -> if (inRunProps) {
                    val value = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                    runItalic = value == null || value != "0" && value != "false"
                }
                "u" -> if (inRunProps) {
                    val value = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                    runUnderline = when (value) {
                        "single" -> UnderlineStyle.SINGLE
                        "double" -> UnderlineStyle.DOUBLE
                        "none" -> UnderlineStyle.NONE
                        else -> if (value != null && value != "none") UnderlineStyle.SINGLE else UnderlineStyle.NONE
                    }
                }
                "strike" -> if (inRunProps) {
                    val value = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                    runStrikethrough = value == null || value != "0" && value != "false"
                }
                "rFonts" -> if (inRunProps) {
                    runFontName = attributes.getValue(NS_W, "ascii")
                        ?: attributes.getValue("w:ascii")
                        ?: attributes.getValue(NS_W, "hAnsi")
                        ?: attributes.getValue("w:hAnsi")
                        ?: attributes.getValue(NS_W, "eastAsia")
                        ?: attributes.getValue("w:eastAsia")
                }
                "sz" -> if (inRunProps) {
                    val value = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                    // Word stores font size in half-points
                    runFontSize = value?.toDoubleOrNull()?.let { it / 2.0 }
                }
                "color" -> if (inRunProps) {
                    val value = attributes.getValue(NS_W, "val")
                        ?: attributes.getValue("w:val")
                    if (value != null && value != "auto") {
                        runColor = OfficeColor.Rgb.fromHex(value)
                    }
                }
                "t" -> {
                    inText = true
                    textBuffer.clear()
                }
            }
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            if (inText) textBuffer.append(ch, start, length)
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            when (localName) {
                "t" -> {
                    inText = false
                    val text = textBuffer.toString()
                    if (currentParagraph != null) {
                        val run = currentParagraph!!.addRun(text)
                        run.style.bold = runBold
                        run.style.italic = runItalic
                        run.style.underline = runUnderline
                        run.style.strikethrough = runStrikethrough
                        run.style.fontName = runFontName
                        run.style.fontSize = runFontSize
                        run.style.color = runColor
                        currentRun = run
                    }
                }
                "rPr" -> inRunProps = false
                "pPr" -> {
                    inParaProps = false
                    currentParagraph?.alignment = paraAlignment
                    currentParagraph?.styleId = paraStyleId
                    currentParagraph?.paragraphStyle?.let { ps ->
                        ps.alignment = paraAlignment
                        ps.indentLeft = paraIndentLeft
                        ps.indentRight = paraIndentRight
                        ps.indentFirstLine = paraIndentFirstLine
                        ps.indentHanging = paraIndentHanging
                        ps.spaceBefore = paraSpaceBefore
                        ps.spaceAfter = paraSpaceAfter
                        ps.lineSpacing = paraLineSpacing
                        ps.lineSpacingRule = paraLineSpacingRule
                    }
                    // List style
                    if (paraNumId != null && paraNumId != 0) {
                        val listType = if (paraNumId == NumberingWriter.BULLET_NUM_ID) ListType.BULLET else ListType.NUMBERED
                        currentParagraph?.listStyle = ListStyle(listType, paraIlvl, paraNumId!!)
                    }
                }
                "tcPr" -> {
                    inCellProps = false
                    currentCell?.width = cellWidth
                    currentCell?.gridSpan = cellGridSpan
                    currentCell?.verticalMerge = cellVMerge
                }
                "tc" -> {
                    inTableCell = false
                    currentCell = null
                }
                "tr" -> currentRow = null
                "tbl" -> {
                    inTable = false
                    currentTable = null
                }
                "hyperlink" -> {
                    inHyperlink = false
                    hyperlinkRelId = null
                }
                "sectPr" -> inSectPr = false
                "r" -> currentRun = null
                "p" -> currentParagraph = null
            }
        }

        private fun resetRunProps() {
            runBold = false
            runItalic = false
            runUnderline = UnderlineStyle.NONE
            runStrikethrough = false
            runFontName = null
            runFontSize = null
            runColor = null
        }

        private fun getAttr(attributes: Attributes, name: String): String? =
            attributes.getValue(NS_W, name) ?: attributes.getValue("w:$name")

        companion object {
            private const val NS_W = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
            private const val NS_R = "http://schemas.openxmlformats.org/officeDocument/2006/relationships"
        }
    }
}
