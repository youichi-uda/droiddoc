package com.droidoffice.doc.core

/**
 * A text run within a paragraph.
 * Each run has its own formatting (RunStyle).
 */
class Run(
    var text: String = "",
) {
    val style = RunStyle()

    fun style(block: RunStyle.() -> Unit) {
        style.block()
    }
}
