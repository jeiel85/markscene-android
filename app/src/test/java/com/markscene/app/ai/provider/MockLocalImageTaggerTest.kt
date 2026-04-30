package com.markscene.app.ai.provider

import org.junit.Test
import org.junit.Assert.*

class MockLocalImageTaggerTest {

    @Test
    fun `mock tagger returns non-empty tags`() {
        val tagger = MockLocalImageTagger()
        val tags = tagger.generateTags("content://test/image.jpg")
        
        assertNotNull(tags)
        assertTrue(tags.isNotEmpty())
    }

    @Test
    fun `mock tagger returns tags with confidence`() {
        val tagger = MockLocalImageTagger()
        val tags = tagger.generateTags("content://test/image.jpg")
        
        tags.forEach { tag ->
            assertTrue(tag.confidence > 0f)
            assertTrue(tag.confidence <= 1f)
        }
    }

    @Test
    fun `mock tagger tags have valid labels`() {
        val tagger = MockLocalImageTagger()
        val tags = tagger.generateTags("content://test/image.jpg")
        
        tags.forEach { tag ->
            assertNotNull(tag.label)
            assertTrue(tag.label.isNotEmpty())
        }
    }
}
