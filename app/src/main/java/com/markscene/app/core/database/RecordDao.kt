package com.markscene.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Transaction
    @Query("SELECT * FROM photo_records ORDER BY createdAt DESC")
    fun observeAllRecords(): Flow<List<PhotoRecordWithTags>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT r.* FROM photo_records r
        LEFT JOIN photo_tags t ON t.recordId = r.id
        WHERE :query = ''
           OR LOWER(COALESCE(r.title, '')) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(COALESCE(r.memo, '')) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(COALESCE(r.ocrText, '')) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(COALESCE(t.name, '')) LIKE '%' || LOWER(:query) || '%'
        ORDER BY r.createdAt DESC
        """
    )
    fun searchRecords(query: String): Flow<List<PhotoRecordWithTags>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PhotoRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<PhotoTagEntity>)

    @Query("DELETE FROM photo_records WHERE id = :recordId")
    suspend fun deleteRecord(recordId: String)

    @Query("DELETE FROM photo_tags WHERE recordId = :recordId")
    suspend fun deleteTagsForRecord(recordId: String)

    @Transaction
    @Query(
        """
        SELECT DISTINCT r.* FROM photo_records r
        INNER JOIN photo_tags t ON t.recordId = r.id
        WHERE LOWER(t.name) = LOWER(:tagName)
        ORDER BY r.createdAt DESC
        """
    )
    fun observeRecordsByTag(tagName: String): Flow<List<PhotoRecordWithTags>>
}
