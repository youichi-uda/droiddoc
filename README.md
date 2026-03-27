# DroidDoc — Android Word Library for Kotlin

[![License: BSL 1.1](https://img.shields.io/badge/License-BSL_1.1-blue.svg)](LICENSE)
[![Android API 26+](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org)
[![](https://jitpack.io/v/youichi-uda/droiddoc.svg)](https://jitpack.io/#youichi-uda/droiddoc)

**Read and write Word .docx files natively on Android.** Kotlin-first API, SAX streaming for low memory usage — built specifically for Android, not ported from Java desktop libraries.

> **$99/year** for commercial use. Free for personal, open source, NPO, and education.
> An affordable alternative to Aspose.Words for Android ($1,175+/year).

## Why DroidDoc?

| | DroidDoc | Aspose.Words Android | Apache POI (Android port) |
|---|---|---|---|
| **Price** | $99/year | $1,175+/year | Free |
| **API** | Kotlin DSL | Java | Java |
| **Memory** | SAX streaming | DOM | DOM |
| **Android support** | Native | Yes | Unofficial |
| **Maintained** | Yes | Yes | No official Android |

## Features

### Core
- Read/write **.docx** (Word 2007+ / OOXML)
- **Paragraphs** with text runs and formatting
- **Kotlin DSL** for styles: `para.addRun("text") { bold = true; fontSize = 14.0 }`
- Fonts, colors, bold, italic, underline, strikethrough
- Paragraph alignment (left, center, right, justify)
- Page setup (size, orientation, margins)
- **Built-in styles** (Heading 1-6, Title, Subtitle, Quote)

### Document Elements
- **Tables** with rows, cells, column span, cell shading
- **Images** (PNG, JPEG, WebP)
- **Lists** (bullet and numbered, multi-level)
- **Hyperlinks**
- **Headers and footers**
- **Sections** with individual page setup

### Advanced
- **Password protection** (AES-256 encryption)
- **HTML export** (with XSS escaping)
- **Plain text extraction**
- **Coroutines:** `suspend fun` async API

## Quick Start

### Installation (Gradle + JitPack)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.youichi-uda:droiddoc:0.1.0-SNAPSHOT")
}
```

### Create a Document

```kotlin
val doc = Document()

// Title
doc.addParagraph("Monthly Report").styleId = BuiltInStyle.TITLE.styleId

// Styled paragraph
val para = doc.addParagraph()
para.addRun("Bold text ") { bold = true }
para.addRun("Red text") { color = OfficeColor.Rgb(255, 0, 0) }
para.alignment = ParagraphAlignment.CENTER

// Table
val table = doc.addTable()
table.addRow("Name", "Score")
table.addRow("Alice", "95")
table.addRow("Bob", "87")

// Bullet list
doc.addParagraph("Item 1").listStyle = ListStyle(ListType.BULLET, 0)
doc.addParagraph("Item 2").listStyle = ListStyle(ListType.BULLET, 0)

// Header/Footer
doc.setHeader().addParagraph("Confidential")
doc.setFooter().addParagraph("Page 1")

// Save
doc.save(FileOutputStream("report.docx"))
```

### Read an Existing Document

```kotlin
val doc = Document.open(FileInputStream("existing.docx"))
for (para in doc.paragraphs) {
    println(para.text)
}
```

### Password-Protected Documents

```kotlin
// Save with password
doc.save(outputStream, "mypassword")

// Open with password
val doc = Document.open(inputStream, "mypassword")
```

### Async API (Coroutines)

```kotlin
val doc = Document.openAsync(inputStream)
doc.saveAsync(outputStream)
```

### HTML / Text Export

```kotlin
val html = HtmlConverter.convert(doc, "Report Title")
val text = TextConverter.convert(doc)
```

## Sample App

The `sample-app/` module demonstrates all features with 8 interactive demos. Build and install:

```bash
./gradlew :sample-app:installDebug
```

## Requirements

- Android API 26+ (Android 8.0 Oreo)
- Kotlin only (no Java interop)

## License

**Business Source License 1.1** — free for personal, open source, non-profit, and educational use. Commercial use requires a [$99/year license](https://y1uda.gumroad.com/l/droiddoc).

After 3 years from each release, the code converts to MIT license.

## Part of DroidOffice

DroidDoc is part of the **DroidOffice** family of Android Office libraries:

| Library | Format | Status |
|---|---|---|
| [DroidXLS](https://github.com/youichi-uda/droidxls) | .xlsx (Excel) | Released |
| **DroidDoc** | .docx (Word) | **Active** |
| DroidSlide | .pptx (PowerPoint) | Planned |
