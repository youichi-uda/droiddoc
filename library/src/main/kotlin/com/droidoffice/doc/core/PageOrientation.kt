package com.droidoffice.doc.core

enum class PageOrientation {
    PORTRAIT,
    LANDSCAPE;

    internal fun toOoxml(): String = when (this) {
        PORTRAIT -> "portrait"
        LANDSCAPE -> "landscape"
    }

    companion object {
        internal fun fromOoxml(value: String): PageOrientation = when (value) {
            "landscape" -> LANDSCAPE
            else -> PORTRAIT
        }
    }
}
