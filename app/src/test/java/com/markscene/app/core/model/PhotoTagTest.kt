package com.markscene.app.core.model

import org.junit.Test
import org.junit.Assert.*

class PhotoTagTest {

    @Test
    fun `create PhotoTag with all fields`() {
        val tag = PhotoTag(
            id = "tag-1",
            recordId = "record-1",
            name = "laptop",
            rawName = "laptop_raw",
            confidence = 0.92f,
            source = TagSource.LocalImageLabel,
            userConfirmed = true,
            createdAt = 1234567890L
        )
        
        assertEquals("tag-1", tag.id)
        assertEquals("record-1", tag.recordId)
        assertEquals("laptop", tag.name)
        assertEquals("laptop_raw", tag.rawName)
        assertEquals(0.92f, tag.confidence!!, 0.01f)
        assertEquals(TagSource.LocalImageLabel, tag.source)
        assertTrue(tag.userConfirmed)
    }

    @Test
    fun `TagSource enums exist`() {
        assertNotNull(TagSource.LocalImageLabel)
        assertNotNull(TagSource.AdvancedAi)
        assertNotNull(TagSource.User)
        assertNotNull(TagSource.Mock)
    }

    @Test
    fun `AnalysisStatus enums exist`() {
        assertNotNull(AnalysisStatus.None)
        assertNotNull(AnalysisStatus.LocalComplete)
        assertNotNull(AnalysisStatus.AdvancedComplete)
        assertNotNull(AnalysisStatus.Failed)
    }
}
