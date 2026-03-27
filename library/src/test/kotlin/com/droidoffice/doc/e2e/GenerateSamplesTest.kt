package com.droidoffice.doc.e2e

import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import com.droidoffice.doc.convert.HtmlConverter
import com.droidoffice.doc.convert.TextConverter
import com.droidoffice.doc.core.*
import com.droidoffice.doc.drawing.ImageFormat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Generates sample .docx files for manual verification in LibreOffice / Word / Google Docs.
 * Output: build/generated-samples/
 */
class GenerateSamplesTest {

    companion object {
        private lateinit var outDir: File

        @BeforeAll
        @JvmStatic
        fun setup() {
            outDir = File("build/generated-samples")
            outDir.mkdirs()
        }
    }

    @Test
    fun `01 basic text`() {
        val doc = Document()
        doc.addParagraph("Hello, DroidDoc!")
        doc.addParagraph("日本語テスト — こんにちは世界！")
        doc.addParagraph("漢字・ひらがな・カタカナ・English mixed")
        doc.addParagraph("Special chars: <>&\"'")
        doc.addParagraph("Emoji: \uD83D\uDE00\uD83D\uDE80\uD83C\uDF1F\uD83D\uDC4D\u2764\uFE0F\uD83C\uDDEF\uD83C\uDDF5")
        save(doc, "01_basic.docx")
    }

    @Test
    fun `02 styles and formatting`() {
        val doc = Document()
        doc.addParagraph("Styles Demo").styleId = BuiltInStyle.TITLE.styleId
        doc.addParagraph("Heading 1").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("Heading 2").styleId = BuiltInStyle.HEADING2.styleId
        doc.addParagraph("Heading 3").styleId = BuiltInStyle.HEADING3.styleId

        val styled = doc.addParagraph()
        styled.addRun("Bold ") { bold = true }
        styled.addRun("Italic ") { italic = true }
        styled.addRun("Underline ") { underline = UnderlineStyle.SINGLE }
        styled.addRun("Strikethrough ") { strikethrough = true }
        styled.addRun("Red text ") { color = OfficeColor.Rgb(255, 0, 0) }
        styled.addRun("Blue 20pt Arial") { color = OfficeColor.Rgb(0, 0, 255); fontSize = 20.0; fontName = "Arial" }

        val mixed = doc.addParagraph()
        mixed.addRun("Bold+Italic+Underline") { bold = true; italic = true; underline = UnderlineStyle.SINGLE }

        doc.addParagraph("Left aligned (default)")
        doc.addParagraph("Center aligned").alignment = ParagraphAlignment.CENTER
        doc.addParagraph("Right aligned").alignment = ParagraphAlignment.RIGHT
        doc.addParagraph("Justified text — Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.").alignment = ParagraphAlignment.JUSTIFY

        val indented = doc.addParagraph("Indented paragraph (left=720, firstLine=360)")
        indented.paragraphStyle.indentLeft = 720
        indented.paragraphStyle.indentFirstLine = 360

        val spaced = doc.addParagraph("Spaced paragraph (before=480, after=240)")
        spaced.paragraphStyle.spaceBefore = 480
        spaced.paragraphStyle.spaceAfter = 240

        save(doc, "02_styles.docx")
    }

    @Test
    fun `03 tables`() {
        val doc = Document()
        doc.addParagraph("Table Demo").styleId = BuiltInStyle.HEADING1.styleId

        val table = doc.addTable()
        val header = table.addRow("Name", "Department", "City", "Salary")
        for (cell in header.cells) {
            cell.shadingColor = OfficeColor.Rgb(47, 85, 151)
        }
        table.addRow("田中太郎", "Engineering", "東京", "$120,000")
        table.addRow("鈴木花子", "Marketing", "大阪", "$95,000")
        table.addRow("佐藤次郎", "Sales", "名古屋", "$88,000")
        table.addRow("山田美咲", "HR", "福岡", "$92,000")

        doc.addParagraph("")
        doc.addParagraph("Multi-paragraph Cell Table").styleId = BuiltInStyle.HEADING2.styleId

        val table2 = doc.addTable()
        val row = table2.addRow()
        row.addCell("Simple cell")
        val multiCell = row.addCell("First paragraph")
        multiCell.addParagraph("Second paragraph")
        multiCell.addParagraph("Third paragraph")
        row.addCell("Another simple cell")

        save(doc, "03_tables.docx")
    }

    @Test
    fun `04 lists`() {
        val doc = Document()
        doc.addParagraph("List Demo").styleId = BuiltInStyle.HEADING1.styleId

        doc.addParagraph("Bullet List:").styleId = BuiltInStyle.HEADING2.styleId
        doc.addParagraph("Fruits").listStyle = ListStyle(ListType.BULLET, 0)
        doc.addParagraph("Apples").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("Red Delicious").listStyle = ListStyle(ListType.BULLET, 2)
        doc.addParagraph("Granny Smith").listStyle = ListStyle(ListType.BULLET, 2)
        doc.addParagraph("Bananas").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("Vegetables").listStyle = ListStyle(ListType.BULLET, 0)
        doc.addParagraph("Carrots").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("Broccoli").listStyle = ListStyle(ListType.BULLET, 1)

        doc.addParagraph("Numbered List:").styleId = BuiltInStyle.HEADING2.styleId
        doc.addParagraph("Plan the project").listStyle = ListStyle(ListType.NUMBERED, 0)
        doc.addParagraph("Define scope").listStyle = ListStyle(ListType.NUMBERED, 1)
        doc.addParagraph("Set timeline").listStyle = ListStyle(ListType.NUMBERED, 1)
        doc.addParagraph("Execute tasks").listStyle = ListStyle(ListType.NUMBERED, 0)
        doc.addParagraph("Review results").listStyle = ListStyle(ListType.NUMBERED, 0)

        save(doc, "04_lists.docx")
    }

    @Test
    fun `05 header footer`() {
        val doc = Document()
        doc.setHeader().addParagraph("DroidDoc — Confidential | March 2026")
        doc.setFooter().addParagraph("Page 1 of 1 | Generated by DroidDoc Library")

        doc.addParagraph("Header & Footer Demo").styleId = BuiltInStyle.TITLE.styleId
        doc.addParagraph("This document has a header and footer. Check the top and bottom of the page.")
        doc.addParagraph("The header says: DroidDoc — Confidential | March 2026")
        doc.addParagraph("The footer says: Page 1 of 1 | Generated by DroidDoc Library")

        save(doc, "05_header_footer.docx")
    }

    @Test
    fun `06 page setup`() {
        val doc = Document()
        doc.pageSetup.orientation = PageOrientation.LANDSCAPE
        doc.pageSetup.marginTop = 720
        doc.pageSetup.marginBottom = 720
        doc.pageSetup.marginLeft = 1440
        doc.pageSetup.marginRight = 1440

        doc.addParagraph("Landscape Page Setup Demo").styleId = BuiltInStyle.TITLE.styleId
        doc.addParagraph("This document is in LANDSCAPE orientation with custom margins.")
        doc.addParagraph("Top/Bottom: 0.5 inch, Left/Right: 1 inch")
        doc.addParagraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(10))

        save(doc, "06_landscape.docx")
    }

    @Test
    fun `07 password protected`() {
        val doc = Document()
        doc.addParagraph("Password Protected Document").styleId = BuiltInStyle.TITLE.styleId
        doc.addParagraph("This file is encrypted with AES-256.")
        doc.addParagraph("Password: secret123")
        val table = doc.addTable()
        table.addRow("Field", "Value")
        table.addRow("SSN", "123-45-6789")
        table.addRow("Account", "XXXX-XXXX-1234")

        val file = File(outDir, "07_password_secret123.docx")
        file.outputStream().use { doc.save(it, "secret123") }
        println("Generated: ${file.absolutePath} (${file.length()} bytes)")
    }

    @Test
    fun `08 html export`() {
        val doc = Document()
        doc.addParagraph("HTML Export Test").styleId = BuiltInStyle.HEADING1.styleId
        val p = doc.addParagraph()
        p.addRun("Bold intro ") { bold = true }
        p.addRun("with green color") { color = OfficeColor.Rgb(0, 128, 0) }
        doc.addParagraph("XSS test: <script>alert('xss')</script>")

        val table = doc.addTable()
        table.addRow("Product", "Price")
        table.addRow("Widget", "$19.99")
        table.addRow("Gadget", "$49.50")

        val html = HtmlConverter.convert(doc, "DroidDoc HTML Export")
        File(outDir, "08_export.html").writeText(html)

        val text = TextConverter.convert(doc)
        File(outDir, "08_export.txt").writeText(text)

        save(doc, "08_export_source.docx")
        println("HTML: ${File(outDir, "08_export.html").absolutePath}")
        println("Text: ${File(outDir, "08_export.txt").absolutePath}")
    }

    @Test
    fun `09 full report`() {
        val doc = Document()
        doc.pageSetup.marginTop = 1000
        doc.pageSetup.marginBottom = 1000
        doc.pageSetup.marginLeft = 1200
        doc.pageSetup.marginRight = 1200

        doc.setHeader().addParagraph("Monthly Report — March 2026 | Confidential")
        doc.setFooter().addParagraph("Generated by DroidDoc | droidoffice.abyo.net")

        doc.addParagraph("Monthly Sales Report").styleId = BuiltInStyle.TITLE.styleId
        doc.addParagraph("Q1 2026 Summary").styleId = BuiltInStyle.SUBTITLE.styleId

        doc.addParagraph("Overview").styleId = BuiltInStyle.HEADING1.styleId
        val intro = doc.addParagraph()
        intro.addRun("This report summarizes ")
        intro.addRun("quarterly sales data") { bold = true; underline = UnderlineStyle.SINGLE }
        intro.addRun(" for the first quarter of 2026. Revenue grew ")
        intro.addRun("15% year-over-year") { bold = true; color = OfficeColor.Rgb(0, 128, 0) }
        intro.addRun(", driven by expansion into new markets.")

        doc.addParagraph("Sales Data").styleId = BuiltInStyle.HEADING2.styleId
        val table = doc.addTable()
        val headerRow = table.addRow("Month", "Revenue", "Growth", "Region")
        for (cell in headerRow.cells) {
            cell.shadingColor = OfficeColor.Rgb(47, 85, 151)
        }
        table.addRow("January", "¥18,000,000", "+5.0%", "関東")
        table.addRow("February", "¥20,250,000", "+12.5%", "関西")
        table.addRow("March", "¥21,300,000", "+5.2%", "中部")

        doc.addParagraph("Key Highlights").styleId = BuiltInStyle.HEADING2.styleId
        doc.addParagraph("Revenue Performance").listStyle = ListStyle(ListType.BULLET, 0)
        doc.addParagraph("Record Q1 revenue of ¥59,550,000").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("15% year-over-year growth").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("All regions exceeded targets").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("Market Expansion").listStyle = ListStyle(ListType.BULLET, 0)
        doc.addParagraph("New office in Nagoya region").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("Partnership with 3 new distributors").listStyle = ListStyle(ListType.BULLET, 1)

        doc.addParagraph("Next Steps").styleId = BuiltInStyle.HEADING2.styleId
        doc.addParagraph("Finalize Q2 revenue targets").listStyle = ListStyle(ListType.NUMBERED, 0)
        doc.addParagraph("Launch Nagoya marketing campaign").listStyle = ListStyle(ListType.NUMBERED, 0)
        doc.addParagraph("Review pricing strategy for enterprise tier").listStyle = ListStyle(ListType.NUMBERED, 0)
        doc.addParagraph("Prepare board presentation for April").listStyle = ListStyle(ListType.NUMBERED, 0)

        doc.addParagraph("Conclusion").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("Q1 2026 was a strong quarter with record revenue and successful market expansion. " +
            "The team is well-positioned for continued growth in Q2.")

        save(doc, "09_full_report.docx")

        val html = HtmlConverter.convert(doc, "Monthly Sales Report — Q1 2026")
        File(outDir, "09_full_report.html").writeText(html)
    }

    private fun save(doc: Document, filename: String) {
        val file = File(outDir, filename)
        file.outputStream().use { doc.save(it) }
        println("Generated: ${file.absolutePath} (${file.length()} bytes)")
    }
}
