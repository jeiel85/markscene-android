package com.markscene.app.data.record

import com.markscene.app.core.database.PhotoRecordEntity
import com.markscene.app.core.database.PhotoRecordWithTags
import com.markscene.app.core.database.PhotoTagEntity
import com.markscene.app.core.database.RecordDao
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomRecordRepository(
    private val recordDao: RecordDao
) {
    fun observeRecords(): Flow<List<PhotoRecord>> =
        recordDao.observeAllRecords().map { rows -> rows.map { it.toModel() } }

    fun search(query: String): Flow<List<PhotoRecord>> =
        recordDao.searchRecords(query).map { rows -> rows.map { it.toModel() } }

    suspend fun saveRecord(record: PhotoRecord) {
        recordDao.insertRecord(
            PhotoRecordEntity(
                id = record.id,
                imageUri = record.imageUri,
                title = record.title,
                memo = record.memo,
                createdAt = record.createdAt,
                updatedAt = record.updatedAt,
                analysisStatus = record.analysisStatus.name
            )
        )
        recordDao.deleteTagsForRecord(record.id)
        recordDao.insertTags(
            record.tags.map { tag ->
                PhotoTagEntity(
                    id = tag.id,
                    recordId = tag.recordId,
                    name = tag.name,
                    rawName = tag.rawName,
                    source = tag.source.name,
                    confidence = tag.confidence,
                    userConfirmed = tag.userConfirmed,
                    createdAt = tag.createdAt
                )
            }
        )
    }

    suspend fun deleteRecord(recordId: String) {
        recordDao.deleteRecord(recordId)
    }
}

private fun PhotoRecordWithTags.toModel(): PhotoRecord =
    PhotoRecord(
        id = record.id,
        imageUri = record.imageUri,
        title = record.title,
        memo = record.memo,
        createdAt = record.createdAt,
        updatedAt = record.updatedAt,
        analysisStatus = AnalysisStatus.valueOf(record.analysisStatus),
        tags = tags.map { tag ->
            PhotoTag(
                id = tag.id,
                recordId = tag.recordId,
                name = tag.name,
                rawName = tag.rawName,
                source = TagSource.valueOf(tag.source),
                confidence = tag.confidence,
                userConfirmed = tag.userConfirmed,
                createdAt = tag.createdAt
            )
        }
    )
