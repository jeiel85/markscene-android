package com.markscene.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordFtsDao {

    /**
     * FTS MATCH 검색 - prefix query 지원.
     * 반환된 RecordFtsEntity에서 recordId를 추출하여 사용합니다.
     */
    @Query("""
        SELECT * FROM records_fts
        WHERE records_fts MATCH :query
    """)
    suspend fun search(query: String): List<RecordFtsEntity>

    /**
     * FTS 인덱스에 레코드 삽입/갱신.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecordFtsEntity)

    /**
     * FTS 인덱스에서 레코드 삭제.
     */
    @Query("DELETE FROM records_fts WHERE recordId = :recordId")
    suspend fun deleteByRecordId(recordId: String)

    /**
     * 여러 레코드 FTS 인덱스에서 삭제.
     */
    @Query("DELETE FROM records_fts WHERE recordId IN (:recordIds)")
    suspend fun deleteByRecordIds(recordIds: List<String>)

    /**
     * 기존 데이터를 FTS 테이블로 마이그레이션 (일회성).
     */
    @Query("""
        INSERT INTO records_fts(recordId, title, memo, ocrText, tagsText)
        SELECT 
            r.id,
            COALESCE(r.title, ''),
            COALESCE(r.memo, ''),
            COALESCE(r.ocrText, ''),
            COALESCE((SELECT GROUP_CONCAT(t.name, ' ') FROM photo_tags t WHERE t.recordId = r.id), '')
        FROM photo_records r
    """)
    suspend fun rebuildIndex()
}
