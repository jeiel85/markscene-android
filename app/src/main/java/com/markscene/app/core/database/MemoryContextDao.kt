package com.markscene.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryContextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(context: MemoryContextEntity)

    @Query("SELECT * FROM memory_contexts WHERE recordId = :recordId")
    suspend fun getByRecordId(recordId: String): MemoryContextEntity?

    @Query("SELECT * FROM memory_contexts WHERE recordId = :recordId")
    fun observeByRecordId(recordId: String): Flow<MemoryContextEntity?>

    @Query("DELETE FROM memory_contexts WHERE recordId = :recordId")
    suspend fun deleteByRecordId(recordId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMemoryTypes(types: List<RecordMemoryTypeCrossRef>)

    @Query("SELECT * FROM record_memory_types WHERE recordId = :recordId")
    fun observeMemoryTypesByRecordId(recordId: String): Flow<List<RecordMemoryTypeCrossRef>>

    @Query("SELECT * FROM record_memory_types WHERE recordId = :recordId")
    suspend fun getMemoryTypesByRecordId(recordId: String): List<RecordMemoryTypeCrossRef>

    @Query("DELETE FROM record_memory_types WHERE recordId = :recordId")
    suspend fun deleteMemoryTypesByRecordId(recordId: String)

    @Query("SELECT DISTINCT r.recordId FROM record_memory_types r WHERE r.memoryType IN (:memoryTypes)")
    fun observeRecordIdsByMemoryTypes(memoryTypes: List<String>): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT m.recordId FROM memory_contexts m
        WHERE m.isWorthRecalling = 1
        ORDER BY m.updatedAt DESC
        """
    )
    fun observeRecallRecordIds(): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT m.recordId FROM memory_contexts m
        WHERE m.isWorthRecalling = 1 AND m.primaryMemoryType IN (:memoryTypes)
        ORDER BY m.updatedAt DESC
        """
    )
    fun observeRecallRecordIdsByTypes(memoryTypes: List<String>): Flow<List<String>>
}
