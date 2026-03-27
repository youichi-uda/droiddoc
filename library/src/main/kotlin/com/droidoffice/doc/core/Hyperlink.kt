package com.droidoffice.doc.core

/**
 * A hyperlink in a paragraph.
 */
class Hyperlink(
    var url: String,
    var text: String,
) {
    val style = RunStyle()

    fun style(block: RunStyle.() -> Unit) {
        style.block()
    }
}
