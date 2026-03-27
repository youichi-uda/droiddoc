package com.droidoffice.doc.exception

import com.droidoffice.core.exception.DroidOfficeException

/**
 * Base exception for DroidDoc-specific errors.
 */
open class DroidDocException(
    message: String,
    cause: Throwable? = null,
) : DroidOfficeException(message, cause)
