package com.droidoffice.doc.core

/**
 * A paragraph in a document, containing one or more text runs.
 */
class Paragraph {

    private val _runs = mutableListOf<Run>()

    val runs: List<Run> get() = _runs

    var alignment: ParagraphAlignment = ParagraphAlignment.LEFT

    /** Built-in style ID (e.g. "Heading1"). Null means default/Normal. */
    var styleId: String? = null

    /** List formatting. Null = not a list item. */
    var listStyle: ListStyle? = null

    /** Hyperlinks in this paragraph. */
    private val _hyperlinks = mutableListOf<Hyperlink>()
    val hyperlinks: List<Hyperlink> get() = _hyperlinks

    fun addHyperlink(url: String, text: String): Hyperlink {
        val link = Hyperlink(url, text)
        _hyperlinks.add(link)
        return link
    }

    /** Paragraph-level formatting. */
    val paragraphStyle = ParagraphStyle()

    fun paragraphStyle(block: ParagraphStyle.() -> Unit) {
        paragraphStyle.block()
    }

    fun addRun(text: String = ""): Run {
        val run = Run(text)
        _runs.add(run)
        return run
    }

    fun addRun(text: String, block: RunStyle.() -> Unit): Run {
        val run = Run(text)
        run.style.block()
        _runs.add(run)
        return run
    }

    /** Get the concatenated text of all runs. */
    val text: String get() = _runs.joinToString("") { it.text }
}
