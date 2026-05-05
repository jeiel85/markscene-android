package com.markscene.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.SmartAlbum
import com.markscene.app.data.record.RoomRecordRepository
import com.markscene.app.data.repository.SmartAlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 스마트 앨범 ViewModel
 */
class SmartAlbumViewModel(
    private val smartAlbumRepository: SmartAlbumRepository,
    private val recordRepository: RoomRecordRepository
) : ViewModel() {
    
    private val _albums = MutableStateFlow<List<SmartAlbum>>(emptyList())
    val albums: StateFlow<List<SmartAlbum>> = _albums.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadAlbums()
    }
    
    private fun loadAlbums() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                smartAlbumRepository.getAllAlbums().collect { albumList ->
                    _albums.value = albumList
                }
            } catch (e: Exception) {
                // TODO: 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 스마트 앨범 자동 생성
     */
    fun generateSmartAlbums() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val records = recordRepository.observeRecords().first()
                smartAlbumRepository.generateDateBasedAlbums(records)
                smartAlbumRepository.generateTagBasedAlbums(records)
            } catch (e: Exception) {
                // TODO: 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 특정 앨범의 사진 목록 조회
     */
    suspend fun getAlbumRecords(albumId: String): List<PhotoRecord> {
        return smartAlbumRepository.getAlbumRecords(albumId)
    }
}
