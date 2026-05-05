package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 스마트 앨범 엔티티
 */
@Entity(tableName = "smart_albums")
data class SmartAlbumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val coverImageUri: String? = null,
    val albumType: String, // AlbumType enum의 문자열 표현
    val createdAt: Long = System.currentTimeMillis()
)
