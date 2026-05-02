package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * AI가 제안한 태그를 사용자가 특정 단어로 고쳐 부르고 싶을 때 사용하는 별칭 데이터
 * 예: "Portable computer" -> "노트북"
 */
@Entity(tableName = "tag_corrections")
data class TagCorrectionEntity(
    @PrimaryKey val originalName: String, // AI가 제안한 원래 이름 (소문자 정규화)
    val correctedName: String,             // 사용자가 지정한 이름
    val usageCount: Int = 1,              // 사용 횟수 (학습 가중치)
    val updatedAt: Long = System.currentTimeMillis()
)
