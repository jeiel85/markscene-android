package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advanced_analysis")
data class AdvancedAnalysisEntity(
    @PrimaryKey val id: String,
    val recordId: String,
    val provider: String,
    val sceneSummary: String,
    val createdAt: Long
)
