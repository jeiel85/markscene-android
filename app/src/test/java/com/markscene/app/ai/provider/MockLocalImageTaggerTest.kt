package com.markscene.app.ai.provider

import android.net.Uri
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class MockLocalImageTaggerTest {

    @Test
    fun `mock tagger returns non-empty tags`() = runBlocking {
        val tagger = MockLocalImageTagger()
        val mockUri = mockk<Uri>()
        val tags = tagger.generateTags(mockUri)
        
        assertNotNull(tags)
        assertTrue(tags.isNotEmpty())
    }

    @Test
    fun `mock tagger returns tags with confidence`() = runBlocking {
        val tagger = MockLocalImageTagger()
        val mockUri = mockk<Uri>()
        val tags = tagger.generateTags(mockUri)
        
        tags.forEach { tag ->
            val confidence = tag.confidence
            if (confidence != null) {
                assertTrue(confidence > 0f)
                assertTrue(confidence <= 1f)
            }
        }
    }

    @Test
    fun `mock tagger tags have valid names`() = runBlocking {
        val tagger = MockLocalImageTagger()
        val mockUri = mockk<Uri>()
        val tags = tagger.generateTags(mockUri)
        
        tags.forEach { tag ->
            assertNotNull(tag.name)
            assertTrue(tag.name.isNotEmpty())
        }
    }
}
