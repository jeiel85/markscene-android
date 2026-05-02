package com.markscene.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TagCorrectionDao {
    @Query("SELECT * FROM tag_corrections ORDER BY usageCount DESC")
    fun getAllCorrections(): Flow<List<TagCorrectionEntity>>

    @Query("SELECT * FROM tag_corrections WHERE originalName = :originalName")
    suspend fun getCorrection(originalName: String): TagCorrectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(correction: TagCorrectionEntity)

    @Query("DELETE FROM tag_corrections WHERE originalName = :originalName")
    suspend fun delete(originalName: String)

    @Query("UPDATE tag_corrections SET usageCount = usageCount + 1, updatedAt = :now WHERE originalName = :originalName")
    suspend fun incrementUsage(originalName: String, now: Long = System.currentTimeMillis())
}
