package com.droidoffice.doc.drawing

/**
 * An inline image in a document.
 */
class Picture(
    val data: ByteArray,
    val format: ImageFormat,
) {
    /** Width in EMU (English Metric Units). 1 inch = 914400 EMU. */
    var widthEmu: Long = 914400

    /** Height in EMU. */
    var heightEmu: Long = 914400

    /** Description / alt text. */
    var description: String = ""

    constructor(data: ByteArray, format: ImageFormat, widthEmu: Long, heightEmu: Long) : this(data, format) {
        this.widthEmu = widthEmu
        this.heightEmu = heightEmu
    }
}

enum class ImageFormat(val extension: String, val contentType: String) {
    PNG("png", "image/png"),
    JPEG("jpeg", "image/jpeg"),
    WEBP("webp", "image/webp"),
    GIF("gif", "image/gif"),
}
