package com.markscene.app.data.repository

import com.markscene.app.core.database.SmartAlbumDao
import com.markscene.app.core.database.SmartAlbumEntity
import com.markscene.app.core.model.AlbumType
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.SmartAlbum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 스마트 앨범 Repository
 */
class SmartAlbumRepository(
    private val smartAlbumDao: SmartAlbumDao
) {
    
    /**
     * 모든 스마트 앨범 조회
     */
    fun getAllAlbums(): Flow<List<SmartAlbum>> {
        return smartAlbumDao.getAllAlbums().map { entities ->
            entities.map { entity ->
                SmartAlbum(
                    id = entity.id,
                    name = entity.name,
                    coverImageUri = entity.coverImageUri,
                    recordIds = emptyList(),
                    albumType = AlbumType.valueOf(entity.albumType),
                    createdAt = entity.createdAt
                )
            }
        }
    }
    
    /**
     * 앨범의 사진 목록 조회
     */
    suspend fun getAlbumRecords(albumId: String): List<PhotoRecord> {
        // TODO: 앨범에 속한 사진 ID를 조회하는 로직 필요
        // 현재는 임시로 빈 리스트 반환
        return emptyList()
    }
    
    /**
     * 날짜 기반 자동 앨범 생성 (이번 주, 이번 달 등)
     */
    suspend fun generateDateBasedAlbums(records: List<PhotoRecord>) {
        smartAlbumDao.deleteAlbumsByType(AlbumType.DATE_BASED.name)

        val now = System.currentTimeMillis()
        val oneWeekAgo = now - 7 * 24 * 60 * 60 * 1000
        val oneMonthAgo = now - 30 * 24 * 60 * 60 * 1000
        
        // 이번 주 사진
        val thisWeekRecords = records.filter { it.createdAt >= oneWeekAgo }
        if (thisWeekRecords.isNotEmpty()) {
            val album = SmartAlbum(
                id = "date_this_week",
                name = "이번 주",
                coverImageUri = thisWeekRecords.firstOrNull()?.imageUri,
                albumType = AlbumType.DATE_BASED,
                createdAt = now
            )
            insertAlbum(album)
        }
        
        // 이번 달 사진
        val thisMonthRecords = records.filter { it.createdAt >= oneMonthAgo }
        if (thisMonthRecords.isNotEmpty()) {
            val album = SmartAlbum(
                id = "date_this_month",
                name = "이번 달",
                coverImageUri = thisMonthRecords.firstOrNull()?.imageUri,
                albumType = AlbumType.DATE_BASED,
                createdAt = now
            )
            insertAlbum(album)
        }
    }
    
    /**
     * 태그 기반 자동 앨범 생성
     */
    suspend fun generateTagBasedAlbums(records: List<PhotoRecord>) {
        smartAlbumDao.deleteAlbumsByType(AlbumType.TAG_BASED.name)

        val tagGroups = mutableMapOf<String, MutableList<PhotoRecord>>()
        
        records.forEach { record ->
            record.tags.forEach { tag ->
                tagGroups.getOrPut(tag.name) { mutableListOf() }.add(record)
            }
        }
        
        tagGroups.forEach { (tagName, taggedRecords) ->
            if (taggedRecords.size >= 3) { // 3개 이상의 사진이 있는 태그만 앨범 생성
                val album = SmartAlbum(
                    id = "tag_$tagName",
                    name = "#$tagName",
                    coverImageUri = taggedRecords.firstOrNull()?.imageUri,
                    albumType = AlbumType.TAG_BASED,
                    createdAt = System.currentTimeMillis()
                )
                insertAlbum(album)
            }
        }
    }
    
    private suspend fun insertAlbum(album: SmartAlbum) {
        val entity = SmartAlbumEntity(
            id = album.id,
            name = album.name,
            coverImageUri = album.coverImageUri,
            albumType = album.albumType.name,
            createdAt = album.createdAt
        )
        smartAlbumDao.insertAlbum(entity)
    }
}
