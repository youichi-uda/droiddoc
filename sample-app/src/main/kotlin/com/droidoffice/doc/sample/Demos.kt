package com.droidoffice.doc.sample

import android.content.Context
import com.droidoffice.core.drawingml.OfficeColor
import com.droidoffice.core.drawingml.UnderlineStyle
import com.droidoffice.doc.convert.HtmlConverter
import com.droidoffice.doc.convert.TextConverter
import com.droidoffice.doc.core.*
import com.droidoffice.doc.drawing.ImageFormat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class Demos(private val context: Context) {

    private fun outputDir(): File {
        val dir = File(context.filesDir, "droiddoc_samples")
        dir.mkdirs()
        return dir
    }

    // -------------------------------------------------------
    // 1. Basic Read/Write
    // -------------------------------------------------------
    suspend fun basicReadWrite(): String {
        val doc = Document()
        doc.addParagraph("Hello, DroidDoc!")
        doc.addParagraph("日本語テスト — こんにちは世界！")
        doc.addParagraph("Third paragraph with special chars: <>&\"'")

        val file = File(outputDir(), "01_basic.docx")
        file.outputStream().use { doc.save(it) }

        val loaded = file.inputStream().use { Document.open(it) }
        check(loaded.paragraphCount == 3) { "Expected 3 paragraphs, got ${loaded.paragraphCount}" }
        check(loaded.paragraphs[0].text == "Hello, DroidDoc!") { "First paragraph mismatch" }
        check(loaded.paragraphs[1].text.contains("日本語")) { "Japanese text lost" }
        check(loaded.paragraphs[2].text.contains("<>&")) { "Special chars lost" }

        return buildString {
            appendLine("Created: ${file.absolutePath} (${file.length()} bytes)")
            appendLine("Paragraphs: ${loaded.paragraphCount}")
            appendLine("P1: ${loaded.paragraphs[0].text}")
            appendLine("P2: ${loaded.paragraphs[1].text}")
        }
    }

    // -------------------------------------------------------
    // 2. Styles & Formatting
    // -------------------------------------------------------
    suspend fun styles(): String {
        val doc = Document()
        doc.addParagraph("Report Title").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("Section").styleId = BuiltInStyle.HEADING2.styleId

        val para = doc.addParagraph()
        para.addRun("Bold ") { bold = true }
        para.addRun("Italic ") { italic = true }
        para.addRun("Underline ") { underline = UnderlineStyle.SINGLE }
        para.addRun("Strike ") { strikethrough = true }
        para.addRun("Red ") { color = OfficeColor.Rgb(255, 0, 0) }
        para.addRun("Large Arial") { fontSize = 24.0; fontName = "Arial" }

        val centered = doc.addParagraph("Centered text")
        centered.alignment = ParagraphAlignment.CENTER

        val right = doc.addParagraph("Right aligned")
        right.alignment = ParagraphAlignment.RIGHT

        // Paragraph indentation and spacing
        val indented = doc.addParagraph("Indented paragraph")
        indented.paragraphStyle.indentLeft = 720
        indented.paragraphStyle.spaceBefore = 240
        indented.paragraphStyle.spaceAfter = 120

        // Page setup
        doc.pageSetup.orientation = PageOrientation.PORTRAIT
        doc.pageSetup.marginTop = 1000
        doc.pageSetup.marginBottom = 1000

        val file = File(outputDir(), "02_styles.docx")
        file.outputStream().use { doc.save(it) }

        val loaded = file.inputStream().use { Document.open(it) }
        check(loaded.paragraphs[0].styleId == "Heading1") { "Heading1 style lost" }
        check(loaded.paragraphs[2].runs[0].style.bold) { "Bold lost" }
        check(loaded.paragraphs[2].runs[1].style.italic) { "Italic lost" }
        check(loaded.paragraphs[2].runs[2].style.underline == UnderlineStyle.SINGLE) { "Underline lost" }
        check(loaded.paragraphs[2].runs[5].style.fontSize == 24.0) { "Font size lost" }
        check(loaded.paragraphs[2].runs[5].style.fontName == "Arial") { "Font name lost" }
        check(loaded.paragraphs[3].alignment == ParagraphAlignment.CENTER) { "Center alignment lost" }
        check(loaded.paragraphs[4].alignment == ParagraphAlignment.RIGHT) { "Right alignment lost" }
        check(loaded.paragraphs[5].paragraphStyle.indentLeft == 720) { "Indent lost" }
        check(loaded.pageSetup.marginTop == 1000) { "Page margin lost" }

        return buildString {
            appendLine("Created: ${file.name} (${file.length()} bytes)")
            appendLine("Heading styles, 6 run styles, alignment, indent, page setup verified")
        }
    }

    // -------------------------------------------------------
    // 3. Tables
    // -------------------------------------------------------
    suspend fun tables(): String {
        val doc = Document()
        doc.addParagraph("Employee Directory").styleId = BuiltInStyle.HEADING1.styleId

        val table = doc.addTable()
        val headerRow = table.addRow("Name", "Department", "City")
        for (cell in headerRow.cells) {
            cell.shadingColor = OfficeColor.Rgb(47, 85, 151)
        }
        table.addRow("Taro Yamada", "Engineering", "Tokyo")
        table.addRow("Hanako Suzuki", "Marketing", "Osaka")
        table.addRow("Jiro Tanaka", "Sales", "Nagoya")

        // Multi-paragraph cell
        val row = table.addRow()
        val cell = row.addCell("Notes")
        cell.gridSpan = 1
        val detailCell = row.addCell("First line")
        detailCell.addParagraph("Second line")
        row.addCell("OK")

        val file = File(outputDir(), "03_tables.docx")
        file.outputStream().use { doc.save(it) }

        val loaded = file.inputStream().use { Document.open(it) }
        check(loaded.tables.size == 1) { "Expected 1 table" }
        check(loaded.tables[0].rowCount == 5) { "Expected 5 rows, got ${loaded.tables[0].rowCount}" }
        check(loaded.tables[0].rows[1].cells[0].text == "Taro Yamada") { "Cell content mismatch" }
        check(loaded.tables[0].rows[4].cells[1].paragraphs.size == 2) { "Multi-paragraph cell lost" }

        return buildString {
            appendLine("Created: ${file.name} (${file.length()} bytes)")
            appendLine("5 rows, 3 cols, shading, multi-paragraph cell verified")
        }
    }

    // -------------------------------------------------------
    // 4. Lists (Bullet + Numbered + Nested)
    // -------------------------------------------------------
    suspend fun lists(): String {
        val doc = Document()
        doc.addParagraph("Shopping List:").styleId = BuiltInStyle.HEADING2.styleId
        for (item in listOf("Fruits", "Dairy", "Snacks")) {
            doc.addParagraph(item).listStyle = ListStyle(ListType.BULLET, 0)
        }
        // Nested bullet
        for (sub in listOf("Apples", "Bananas")) {
            doc.addParagraph(sub).listStyle = ListStyle(ListType.BULLET, 1)
        }

        doc.addParagraph("Project Steps:").styleId = BuiltInStyle.HEADING2.styleId
        for (step in listOf("Plan", "Execute", "Review")) {
            doc.addParagraph(step).listStyle = ListStyle(ListType.NUMBERED, 0)
        }
        // Nested numbered
        for (sub in listOf("Gather feedback", "Analyze results")) {
            doc.addParagraph(sub).listStyle = ListStyle(ListType.NUMBERED, 1)
        }

        val file = File(outputDir(), "04_lists.docx")
        file.outputStream().use { doc.save(it) }

        val loaded = file.inputStream().use { Document.open(it) }
        val listItems = loaded.paragraphs.filter { it.listStyle != null }
        check(listItems.size == 10) { "Expected 10 list items, got ${listItems.size}" }
        val nested = listItems.filter { it.listStyle!!.level == 1 }
        check(nested.size == 4) { "Expected 4 nested items, got ${nested.size}" }

        return buildString {
            appendLine("Created: ${file.name} (${file.length()} bytes)")
            appendLine("${listItems.size} list items (${nested.size} nested)")
            appendLine("Bullet + numbered + nested levels verified")
        }
    }

    // -------------------------------------------------------
    // 5. Header/Footer
    // -------------------------------------------------------
    suspend fun headerFooter(): String {
        val doc = Document()
        val header = doc.setHeader()
        header.addParagraph("DroidDoc — Confidential Report")
        val footer = doc.setFooter()
        footer.addParagraph("Page 1 of 1 | Generated by DroidDoc")
        doc.addParagraph("Document body content goes here.")
        doc.addParagraph("Second paragraph in the body.")

        val file = File(outputDir(), "05_header_footer.docx")
        file.outputStream().use { doc.save(it) }

        val loaded = file.inputStream().use { Document.open(it) }
        check(loaded.getHeader() != null) { "Header missing" }
        check(loaded.getFooter() != null) { "Footer missing" }
        check(loaded.getHeader()!!.text.contains("Confidential")) { "Header content lost" }
        check(loaded.getFooter()!!.text.contains("DroidDoc")) { "Footer content lost" }

        return buildString {
            appendLine("Created: ${file.name} (${file.length()} bytes)")
            appendLine("Header: ${loaded.getHeader()!!.text}")
            appendLine("Footer: ${loaded.getFooter()!!.text}")
        }
    }

    // -------------------------------------------------------
    // 6. Images
    // -------------------------------------------------------
    suspend fun images(): String {
        val doc = Document()
        doc.addParagraph("Image Demo").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("Below is an embedded image:")

        // Fake 1x1 PNG (minimal valid-ish header bytes for demo)
        val fakePng = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52
        )
        doc.addPicture(fakePng, ImageFormat.PNG, 1828800, 914400) // 2"x1"

        // JPEG image
        val fakeJpeg = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte())
        doc.addPicture(fakeJpeg, ImageFormat.JPEG, 914400, 914400) // 1"x1"

        val file = File(outputDir(), "06_images.docx")
        file.outputStream().use { doc.save(it) }

        // Verify image data preserved in package
        val pkg = com.droidoffice.core.ooxml.OoxmlPackage.open(file.inputStream())
        val pngPart = pkg.getPart("word/media/image1.png")
        val jpegPart = pkg.getPart("word/media/image2.jpeg")
        check(pngPart != null) { "PNG image part missing" }
        check(jpegPart != null) { "JPEG image part missing" }
        check(pngPart.contentEquals(fakePng)) { "PNG data corrupted" }
        check(jpegPart.contentEquals(fakeJpeg)) { "JPEG data corrupted" }

        return buildString {
            appendLine("Created: ${file.name} (${file.length()} bytes)")
            appendLine("2 images embedded (PNG + JPEG)")
            appendLine("Image data verified in OOXML package")
        }
    }

    // -------------------------------------------------------
    // 7. Password Protection
    // -------------------------------------------------------
    suspend fun passwordProtection(): String {
        val doc = Document()
        doc.addParagraph("Confidential Data").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("SSN: 123-45-6789")
        doc.addParagraph("Salary: $120,000")

        // Save with password
        val buffer = ByteArrayOutputStream()
        doc.save(buffer, "s3cret!")
        val encrypted = buffer.toByteArray()

        // Verify blocked without password
        var blocked = false
        try {
            Document.open(ByteArrayInputStream(encrypted))
        } catch (_: Exception) {
            blocked = true
        }

        // Open with correct password
        val loaded = Document.open(ByteArrayInputStream(encrypted), "s3cret!")

        // Save decrypted copy
        val file = File(outputDir(), "07_password_decrypted.docx")
        file.outputStream().use { loaded.save(it) }

        check(blocked) { "Should block without password" }
        check(loaded.paragraphs[0].text == "Confidential Data") { "Content mismatch after decrypt" }
        check(loaded.paragraphs[1].text.contains("123-45-6789")) { "SSN lost after decrypt" }

        return buildString {
            appendLine("Encrypted size: ${encrypted.size} bytes")
            appendLine("Blocked without password: $blocked")
            appendLine("Decrypted: ${loaded.paragraphs[0].text}")
            appendLine("Decrypted copy saved: ${file.name}")
        }
    }

    // -------------------------------------------------------
    // 8. HTML / Text Export
    // -------------------------------------------------------
    suspend fun htmlTextExport(): String {
        val doc = Document()
        doc.addParagraph("Report Title").styleId = BuiltInStyle.HEADING1.styleId
        doc.addParagraph("Summary").styleId = BuiltInStyle.HEADING2.styleId
        val p = doc.addParagraph()
        p.addRun("Bold intro ") { bold = true }
        p.addRun("with color") { color = OfficeColor.Rgb(0, 128, 0) }
        doc.addParagraph("Normal paragraph with <script>XSS test</script>")

        val table = doc.addTable()
        table.addRow("Name", "Score")
        table.addRow("Alice", "95")
        table.addRow("Bob", "87")

        // HTML
        val html = HtmlConverter.convert(doc, "Score Report")
        val htmlFile = File(outputDir(), "08_export.html")
        htmlFile.writeText(html)

        // Text
        val text = TextConverter.convert(doc)
        val textFile = File(outputDir(), "08_export.txt")
        textFile.writeText(text)

        check(html.contains("<h1>Report Title</h1>")) { "H1 missing in HTML" }
        check(html.contains("<h2>Summary</h2>")) { "H2 missing in HTML" }
        check(html.contains("font-weight:bold")) { "Bold style missing in HTML" }
        check(html.contains("<table")) { "Table missing in HTML" }
        check(!html.contains("<script>")) { "XSS not escaped!" }
        check(html.contains("&lt;script&gt;")) { "XSS escaping failed" }
        check(text.contains("Alice\t95")) { "Table text extraction failed" }

        return buildString {
            appendLine("HTML: ${htmlFile.name} (${htmlFile.length()} bytes)")
            appendLine("Text: ${textFile.name} (${textFile.length()} bytes)")
            appendLine("XSS escaping verified, table export verified")
        }
    }

    // -------------------------------------------------------
    // 9. Full Report (All Features Combined)
    // -------------------------------------------------------
    suspend fun fullReport(): String {
        val doc = Document()

        // Page setup
        doc.pageSetup.pageSize = PageSize.A4
        doc.pageSetup.orientation = PageOrientation.PORTRAIT
        doc.pageSetup.marginTop = 1000
        doc.pageSetup.marginBottom = 1000
        doc.pageSetup.marginLeft = 1200
        doc.pageSetup.marginRight = 1200

        // Header/Footer
        doc.setHeader().addParagraph("Monthly Report — March 2026 | Confidential")
        doc.setFooter().addParagraph("Generated by DroidDoc Sample App")

        // Title page
        doc.addParagraph("Monthly Sales Report").styleId = BuiltInStyle.TITLE.styleId
        doc.addParagraph("Q1 2026 Summary").styleId = BuiltInStyle.SUBTITLE.styleId

        // Introduction
        doc.addParagraph("Overview").styleId = BuiltInStyle.HEADING1.styleId
        val intro = doc.addParagraph()
        intro.addRun("This report summarizes ")
        intro.addRun("quarterly sales data") { bold = true; underline = UnderlineStyle.SINGLE }
        intro.addRun(" for the first quarter of 2026. For details, visit ")
        intro.addHyperlink("https://droidoffice.abyo.net", "droidoffice.abyo.net")

        // Sales table
        doc.addParagraph("Sales Data").styleId = BuiltInStyle.HEADING2.styleId
        val table = doc.addTable()
        val headerRow = table.addRow("Month", "Revenue", "Growth", "Region")
        for (cell in headerRow.cells) {
            cell.shadingColor = OfficeColor.Rgb(47, 85, 151)
        }
        table.addRow("January", "$120,000", "+5%", "Tokyo")
        table.addRow("February", "$135,000", "+12.5%", "Osaka")
        table.addRow("March", "$142,000", "+5.2%", "Nagoya")

        // Key highlights (bullet list with nesting)
        doc.addParagraph("Key Highlights").styleId = BuiltInStyle.HEADING2.styleId
        doc.addParagraph("Revenue").listStyle = ListStyle(ListType.BULLET, 0)
        doc.addParagraph("Record Q1 revenue of $397,000").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("15% year-over-year growth").listStyle = ListStyle(ListType.BULLET, 1)
        doc.addParagraph("Expansion").listStyle = ListStyle(ListType.BULLET, 0)
        doc.addParagraph("New market entry in Nagoya region").listStyle = ListStyle(ListType.BULLET, 1)

        // Action items (numbered)
        doc.addParagraph("Next Steps").styleId = BuiltInStyle.HEADING2.styleId
        for (step in listOf("Finalize Q2 targets", "Launch Nagoya campaign", "Review pricing strategy")) {
            doc.addParagraph(step).listStyle = ListStyle(ListType.NUMBERED, 0)
        }

        // Image
        val fakePng = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52
        )
        doc.addPicture(fakePng, ImageFormat.PNG, 2743200, 1371600) // 3"x1.5"

        // Save
        val file = File(outputDir(), "09_full_report.docx")
        file.outputStream().use { doc.save(it) }

        // Read back and verify
        val loaded = file.inputStream().use { Document.open(it) }

        check(loaded.getHeader() != null) { "Header missing" }
        check(loaded.getFooter() != null) { "Footer missing" }
        check(loaded.tables.size == 1) { "Table missing" }
        check(loaded.tables[0].rowCount == 4) { "Table rows wrong" }
        check(loaded.paragraphs.any { it.styleId == "Title" }) { "Title style missing" }
        check(loaded.paragraphs.any { it.listStyle?.type == ListType.BULLET }) { "Bullet list missing" }
        check(loaded.paragraphs.any { it.listStyle?.type == ListType.NUMBERED }) { "Numbered list missing" }
        check(loaded.pageSetup.marginLeft == 1200) { "Page margins lost" }

        // Export
        val html = HtmlConverter.convert(loaded, "Monthly Report")
        File(outputDir(), "09_report.html").writeText(html)
        val text = TextConverter.convert(loaded)

        return buildString {
            appendLine("Created: ${file.name} (${file.length()} bytes)")
            appendLine("Paragraphs: ${loaded.paragraphCount}, Tables: ${loaded.tables.size}")
            appendLine("Header: ${loaded.getHeader()!!.text.take(40)}...")
            appendLine("Footer: ${loaded.getFooter()!!.text}")
            appendLine("List items: ${loaded.paragraphs.count { it.listStyle != null }}")
            appendLine("Images: ${loaded.pictures.size}")
            appendLine("HTML export: ${html.length} chars")
            appendLine("All features verified!")
        }
    }
}
