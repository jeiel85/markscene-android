package com.markscene.app.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NavigationDepthPolicyTest {
    @Test
    fun `today root has no fallback so app can close intentionally`() {
        assertNull(fallbackRouteAfterBackPop("today"))
    }

    @Test
    fun `top level non-home routes fall back to today when stack is shallow`() {
        assertEquals("today", fallbackRouteAfterBackPop("search"))
        assertEquals("today", fallbackRouteAfterBackPop("recall"))
        assertEquals("today", fallbackRouteAfterBackPop("settings"))
    }

    @Test
    fun `nested routes fall back to today when no previous depth exists`() {
        assertEquals("today", fallbackRouteAfterBackPop("detail/{recordId}"))
        assertEquals("today", fallbackRouteAfterBackPop("privacy_notice"))
    }
}
