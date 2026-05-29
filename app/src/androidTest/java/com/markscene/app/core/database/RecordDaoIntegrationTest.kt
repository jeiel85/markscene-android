package com.markscene.app.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordDaoIntegrationTest {

    private lateinit var database: MarkSceneDatabase
    private lateinit var recordDao: RecordDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MarkSceneDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        recordDao = database.recordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSearchAndDeleteRecord_keepsTagsConsistent() = runBlocking {
        val record = PhotoRecordEntity(
            id = "record-1",
            imageUri = "content://markscene/test-image",
            audioMemoUri = null,
            title = "Desk setup",
            memo = "Keyboard and notebook on the desk",
            createdAt = 2_000L,
            updatedAt = 2_000L,
            analysisStatus = "LocalComplete",
            ocrText = "TODO receipt",
            space = "office"
        )
        val tags = listOf(
            PhotoTagEntity(
                id = "tag-1",
                recordId = record.id,
                name = "keyboard",
                rawName = "Keyboard",
                source = "LocalImageLabel",
                confidence = 0.91f,
                userConfirmed = false,
                createdAt = 2_000L
            ),
            PhotoTagEntity(
                id = "tag-2",
                recordId = record.id,
                name = "desk",
                rawName = "Desk",
                source = "LocalVlm",
                confidence = null,
                userConfirmed = false,
                createdAt = 2_000L
            )
        )

        recordDao.insertRecord(record)
        recordDao.insertTags(tags)

        val tagResults = recordDao.searchRecords("keyboard").first()
        assertEquals(1, tagResults.size)
        assertEquals("record-1", tagResults.single().record.id)
        assertEquals(listOf("keyboard", "desk"), tagResults.single().tags.map { it.name })

        val ocrResults = recordDao.searchRecords("receipt").first()
        assertEquals(1, ocrResults.size)

        recordDao.deleteRecord(record.id)

        assertTrue(recordDao.searchRecords("keyboard").first().isEmpty())
    }
}
