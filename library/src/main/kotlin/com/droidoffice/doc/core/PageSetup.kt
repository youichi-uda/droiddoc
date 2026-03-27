package com.droidoffice.doc.core

/**
 * Page setup (section-level properties).
 * All margin values are in twips (1/1440 inch).
 * Default margins: 1 inch (1440 twips) on all sides.
 */
data class PageSetup(
    var pageSize: PageSize = PageSize.A4,
    var orientation: PageOrientation = PageOrientation.PORTRAIT,
    /** Custom width in twips (used when pageSize = CUSTOM or orientation = LANDSCAPE). */
    var customWidth: Int? = null,
    /** Custom height in twips (used when pageSize = CUSTOM or orientation = LANDSCAPE). */
    var customHeight: Int? = null,
    var marginTop: Int = 1440,
    var marginBottom: Int = 1440,
    var marginLeft: Int = 1440,
    var marginRight: Int = 1440,
    var marginHeader: Int = 720,
    var marginFooter: Int = 720,
) {
    /** Effective width in twips (accounts for orientation). */
    val effectiveWidth: Int
        get() = customWidth ?: if (orientation == PageOrientation.LANDSCAPE) {
            pageSize.heightTwips
        } else {
            pageSize.widthTwips
        }

    /** Effective height in twips (accounts for orientation). */
    val effectiveHeight: Int
        get() = customHeight ?: if (orientation == PageOrientation.LANDSCAPE) {
            pageSize.widthTwips
        } else {
            pageSize.heightTwips
        }
}
