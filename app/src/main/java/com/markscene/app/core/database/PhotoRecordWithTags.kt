package com.markscene.app.core.database

import androidx.room.Embedded
import androidx.room.Relation

data class PhotoRecordWithTags(
    @Embedded val record: PhotoRecordEntity,
    @Relation(parentColumn = "id", entityColumn = "recordId")
    val tags: List<PhotoTagEntity>
)
