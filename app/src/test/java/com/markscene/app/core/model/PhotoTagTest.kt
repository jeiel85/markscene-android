package com.markscene.app.core.model

import org.junit.Test
import org.junit.Assert.*

class PhotoTagTest {

    @Test
    fun `create PhotoTag with all fields`() {
        val tag = PhotoTag(
            id = "tag-1",
            recordId = "record-1",
            label = "laptop",
            confidence = 0.92f,
            source = TagSource.LOCAL,
            isConfirmed = true
        )
        
        assertEquals("tag-1", tag.id)
        assertEquals("record-1", tag.recordId)
        assertEquals("laptop", tag.label)
        assertEquals(0.92f, tag.confidence, 0.01f)
        assertEquals(TagSource.LOCAL, tag.source)
        assertTrue(tag.isConfirmed)
    }

    @Test
    fun `TagSource values are correct`() {
        assertEquals("local", TagSource.LOCAL.value)
        assertEquals("advanced_ai", TagSource.ADVANCED_AI.value)
        assertEquals("user", TagSource.USER.value)
    }

    @Test
    fun `AnalysisStatus values are correct`() {
        assertEquals("none", AnalysisStatus.NONE.value)
        assertEquals("pending", AnalysisStatus.PENDING.value)
        assertEquals("advanced_complete", AnalysisStatus.ADVANCED_COMPLETE.value)
        assertEquals("failed", AnalysisStatus.FAILED.value)
    }
}
