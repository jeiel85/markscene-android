package com.markscene.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoRecordEntity::class, PhotoTagEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MarkSceneDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}
