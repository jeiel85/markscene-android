package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
 * FTS4 가상 테이블 - 레코드 검색을 위한 전문 검색 인덱스.
 *
 * title, memo, ocrText, 그리고 모든 태그 이름을 공백으로 연결한 tagsText를 인덱싱합니다.
 * LIKE + JOIN 검색을 FTS MATCH로 대체하여 수천 개 레코드에서도 빠른 검색을 제공합니다.
 *
 * contentEntity를 사용하지 않고 수동으로 동기화합니다 (tagsText가 photo_tags에서 파생되기 때문).
 */
@Fts4(tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "records_fts")
data class RecordFtsEntity(
    @androidx.room.ColumnInfo(name = "recordId")
    val recordId: String,
    @androidx.room.ColumnInfo(name = "title")
    val title: String,
    @androidx.room.ColumnInfo(name = "memo")
    val memo: String,
    @androidx.room.ColumnInfo(name = "ocrText")
    val ocrText: String,
    @androidx.room.ColumnInfo(name = "tagsText")
    val tagsText: String
)
