package com.markscene.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdvancedAnalysisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(analysis: AdvancedAnalysisEntity)

    @Query("SELECT * FROM advanced_analysis WHERE recordId = :recordId ORDER BY createdAt DESC LIMIT 1")
    fun observeLatest(recordId: String): Flow<AdvancedAnalysisEntity?>

    @Query("DELETE FROM advanced_analysis WHERE recordId = :recordId")
    suspend fun deleteForRecord(recordId: String)
}
