package com.markscene.app.core.model

import kotlinx.serialization.Serializable

/**
 * 스마트 앨범: 날짜/태그 기반 자동 폴더링
 */
@Serializable
data class SmartAlbum(
    val id: String,
    val name: String,
    val coverImageUri: String? = null,
    val recordIds: List<String> = emptyList(),
    val albumType: AlbumType,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 앨범 유형
 */
enum class AlbumType {
    DATE_BASED,    // 날짜 기반 (이번 주, 이번 달 등)
    TAG_BASED,     // 태그 기반 (특정 태그를 가진 사진들)
    SPACE_BASED    // 공간 기반 (특정 공간의 사진들)
}
