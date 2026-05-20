package com.markscene.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PhotoRecordEntity::class,
        PhotoTagEntity::class,
        AdvancedAnalysisEntity::class,
        TagCorrectionEntity::class,
        ChatMessageEntity::class,
        SmartAlbumEntity::class,
        MemoryContextEntity::class,
        RecordMemoryTypeCrossRef::class,
        RecordFtsEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class MarkSceneDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun advancedAnalysisDao(): AdvancedAnalysisDao
    abstract fun tagCorrectionDao(): TagCorrectionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun smartAlbumDao(): SmartAlbumDao
    abstract fun memoryContextDao(): MemoryContextDao
    abstract fun recordFtsDao(): RecordFtsDao
}
