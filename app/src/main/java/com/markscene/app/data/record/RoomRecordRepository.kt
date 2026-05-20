package com.markscene.app.data.record

import com.markscene.app.core.database.AdvancedAnalysisDao
import com.markscene.app.core.database.AdvancedAnalysisEntity
import com.markscene.app.core.database.ChatMessageDao
import com.markscene.app.core.database.ChatMessageEntity
import com.markscene.app.core.database.MemoryContextDao
import com.markscene.app.core.database.MemoryContextEntity
import com.markscene.app.core.database.PhotoRecordEntity
import com.markscene.app.core.database.PhotoRecordWithTags
import com.markscene.app.core.database.PhotoTagEntity
import com.markscene.app.core.database.RecordDao
import com.markscene.app.core.database.RecordFtsDao
import com.markscene.app.core.database.RecordFtsEntity
import com.markscene.app.core.database.RecordMemoryTypeCrossRef
import com.markscene.app.core.model.AdvancedAnalysis
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.ChatMessage
import com.markscene.app.core.model.MemoryContext
import com.markscene.app.core.model.MemorySource
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RoomRecordRepository(
    private val recordDao: RecordDao,
    private val analysisDao: AdvancedAnalysisDao,
    private val chatMessageDao: ChatMessageDao,
    private val memoryContextDao: MemoryContextDao,
    private val ftsDao: RecordFtsDao
) {
    fun observeRecords(): Flow<List<PhotoRecord>> =
        recordDao.observeAllRecords().map { rows -> rows.map { it.toModel() } }

    fun search(query: String): Flow<List<PhotoRecord>> =
        recordDao.searchRecords(query).map { rows -> rows.map { it.toModel() } }

    fun observeRecordsByTag(tagName: String): Flow<List<PhotoRecord>> =
        recordDao.observeRecordsByTag(tagName).map { rows -> rows.map { it.toModel() } }

    fun observeLatestAnalysis(recordId: String): Flow<AdvancedAnalysis?> =
        analysisDao.observeLatest(recordId).map { entity ->
            entity?.let {
                AdvancedAnalysis(
                    id = it.id,
                    recordId = it.recordId,
                    provider = it.provider,
                    sceneSummary = it.sceneSummary,
                    createdAt = it.createdAt
                )
            }
        }

    fun observeMessages(recordId: String): Flow<List<ChatMessage>> =
        chatMessageDao.observeMessagesForRecord(recordId).map { entities ->
            entities.map {
                ChatMessage(it.id, it.recordId, it.role, it.content, it.createdAt)
            }
        }

    suspend fun saveChatMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(
            ChatMessageEntity(message.id, message.recordId, message.role, message.content, message.createdAt)
        )
    }

    suspend fun saveRecord(record: PhotoRecord) {
        recordDao.insertRecord(
            PhotoRecordEntity(
                id = record.id,
                imageUri = record.imageUri,
                audioMemoUri = record.audioMemoUri,
                title = record.title,
                memo = record.memo,
                createdAt = record.createdAt,
                updatedAt = record.updatedAt,
                analysisStatus = record.analysisStatus.name,
                ocrText = record.ocrText,
                space = record.space
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
        // FTS 인덱스 동기화
        ftsDao.insert(
            RecordFtsEntity(
                recordId = record.id,
                title = record.title.orEmpty(),
                memo = record.memo.orEmpty(),
                ocrText = record.ocrText.orEmpty(),
                tagsText = record.tags.joinToString(" ") { it.name }
            )
        )
    }

    suspend fun saveAdvancedAnalysis(analysis: AdvancedAnalysis) {
        analysisDao.upsert(
            AdvancedAnalysisEntity(
                id = analysis.id,
                recordId = analysis.recordId,
                provider = analysis.provider,
                sceneSummary = analysis.sceneSummary,
                createdAt = analysis.createdAt
            )
        )
    }

    suspend fun saveMemoryContext(context: MemoryContext) {
        memoryContextDao.upsert(
            MemoryContextEntity(
                id = context.id,
                recordId = context.recordId,
                primaryMemoryType = context.primaryMemoryType,
                mood = context.mood,
                energy = context.energy,
                contextType = context.contextType,
                isWorthRecalling = context.isWorthRecalling,
                recallReason = context.recallReason,
                createdAt = context.createdAt,
                updatedAt = context.updatedAt
            )
        )
    }

    suspend fun saveMemoryTypes(recordId: String, memoryTypes: List<String>, source: MemorySource, userConfirmed: Boolean) {
        memoryContextDao.deleteMemoryTypesByRecordId(recordId)
        val now = System.currentTimeMillis()
        val crossRefs = memoryTypes.map { type ->
            RecordMemoryTypeCrossRef(
                recordId = recordId,
                memoryType = type,
                source = source.name,
                userConfirmed = userConfirmed,
                createdAt = now
            )
        }
        memoryContextDao.upsertMemoryTypes(crossRefs)
    }

    fun observeMemoryContext(recordId: String): Flow<MemoryContext?> =
        memoryContextDao.observeByRecordId(recordId).map { entity ->
            entity?.let {
                MemoryContext(
                    id = it.id,
                    recordId = it.recordId,
                    primaryMemoryType = it.primaryMemoryType,
                    mood = it.mood,
                    energy = it.energy,
                    contextType = it.contextType,
                    isWorthRecalling = it.isWorthRecalling,
                    recallReason = it.recallReason,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }

    fun observeMemoryTypes(recordId: String): Flow<List<String>> =
        memoryContextDao.observeMemoryTypesByRecordId(recordId).map { entities ->
            entities.map { it.memoryType }
        }

    suspend fun getMemoryContext(recordId: String): MemoryContext? =
        memoryContextDao.getByRecordId(recordId)?.let { entity ->
            MemoryContext(
                id = entity.id,
                recordId = entity.recordId,
                primaryMemoryType = entity.primaryMemoryType,
                mood = entity.mood,
                energy = entity.energy,
                contextType = entity.contextType,
                isWorthRecalling = entity.isWorthRecalling,
                recallReason = entity.recallReason,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }

    suspend fun getMemoryTypes(recordId: String): List<String> =
        memoryContextDao.getMemoryTypesByRecordId(recordId).map { it.memoryType }

    suspend fun deleteRecord(recordId: String) {
        analysisDao.deleteForRecord(recordId)
        chatMessageDao.deleteMessagesForRecord(recordId)
        memoryContextDao.deleteByRecordId(recordId)
        memoryContextDao.deleteMemoryTypesByRecordId(recordId)
        recordDao.deleteRecord(recordId)
    }

    suspend fun deleteRecords(recordIds: List<String>) {
        recordIds.forEach {
            analysisDao.deleteForRecord(it)
            chatMessageDao.deleteMessagesForRecord(it)
            memoryContextDao.deleteByRecordId(it)
            memoryContextDao.deleteMemoryTypesByRecordId(it)
        }
        recordDao.deleteRecords(recordIds)
        recordDao.deleteTagsForRecords(recordIds)
        ftsDao.deleteByRecordIds(recordIds)
    }

    suspend fun updateRecordsSpace(recordIds: List<String>, newSpace: String?) {
        recordDao.updateRecordsSpace(recordIds, newSpace)
    }
}

private fun PhotoRecordWithTags.toModel(): PhotoRecord =
    PhotoRecord(
        id = record.id,
        imageUri = record.imageUri,
        audioMemoUri = record.audioMemoUri,
        title = record.title,
        memo = record.memo,
        createdAt = record.createdAt,
        updatedAt = record.updatedAt,
        analysisStatus = AnalysisStatus.valueOf(record.analysisStatus),
        ocrText = record.ocrText,
        space = record.space,
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
