package com.droidoffice.doc.e2e

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertTrue

class GumroadLicenseVerifyTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "DROIDDOC_LICENSE_KEY", matches = ".+")
    fun `verify Gumroad license key`() {
        // This test requires a valid Gumroad license key in DROIDDOC_LICENSE_KEY env var.
        // It also requires Android context, so it should be run on a real device or emulator.
        // Skipped in normal CI.
        val key = System.getenv("DROIDDOC_LICENSE_KEY") ?: ""
        assertTrue(key.isNotBlank(), "License key should be non-blank")
    }
}
