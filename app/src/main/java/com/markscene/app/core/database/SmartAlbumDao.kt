package com.markscene.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 스마트 앨범 DAO
 */
@Dao
interface SmartAlbumDao {
    
    @Query("SELECT * FROM smart_albums ORDER BY createdAt DESC")
    fun getAllAlbums(): Flow<List<SmartAlbumEntity>>
    
    @Query("SELECT * FROM smart_albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: String): SmartAlbumEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: SmartAlbumEntity)
    
    @Query("DELETE FROM smart_albums WHERE id = :albumId")
    suspend fun deleteAlbum(albumId: String)
    
    @Query("SELECT * FROM smart_albums WHERE albumType = :type")
    fun getAlbumsByType(type: String): Flow<List<SmartAlbumEntity>>

    @Query("DELETE FROM smart_albums WHERE albumType = :type")
    suspend fun deleteAlbumsByType(type: String)
}
