package com.markscene.app.core.model

import org.junit.Test
import org.junit.Assert.*

class PhotoRecordTest {

    @Test
    fun `create PhotoRecord with basic fields`() {
        val record = PhotoRecord(
            id = "test-id-123",
            imageUri = "content://media/external/images/media/123",
            title = "Desk Setup",
            memo = "My workspace setup",
            tags = emptyList(),
            createdAt = 1234567890L,
            updatedAt = 1234567890L,
            analysisStatus = AnalysisStatus.None
        )
        
        assertEquals("test-id-123", record.id)
        assertEquals("content://media/external/images/media/123", record.imageUri)
        assertEquals("Desk Setup", record.title)
        assertEquals("My workspace setup", record.memo)
        assertNotNull(record.tags)
    }

    @Test
    fun `PhotoRecord tags can be modified`() {
        val tag = PhotoTag(
            id = "tag-1",
            recordId = "test-id-123",
            name = "laptop",
            rawName = "laptop",
            confidence = 0.85f,
            source = TagSource.LocalImageLabel,
            userConfirmed = false,
            createdAt = 1234567890L
        )
        
        val record = PhotoRecord(
            id = "test-id-123",
            imageUri = "uri",
            title = "Test",
            memo = null,
            tags = listOf(tag),
            createdAt = 1234567890L,
            updatedAt = 1234567890L,
            analysisStatus = AnalysisStatus.None
        )
        
        assertEquals(1, record.tags.size)
        assertEquals("laptop", record.tags.first().name)
        assertEquals(TagSource.LocalImageLabel, record.tags.first().source)
    }
}
