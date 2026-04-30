package com.markscene.app.data.record

import com.markscene.app.core.model.PhotoRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryRecordRepository {
    private val records = MutableStateFlow<List<PhotoRecord>>(emptyList())

    fun observeRecords(): StateFlow<List<PhotoRecord>> = records.asStateFlow()

    fun saveRecord(record: PhotoRecord) {
        records.value = listOf(record) + records.value
    }

    fun search(query: String): List<PhotoRecord> {
        if (query.isBlank()) return records.value
        val q = query.trim().lowercase()
        return records.value.filter { record ->
            record.title.orEmpty().lowercase().contains(q) ||
                record.memo.orEmpty().lowercase().contains(q) ||
                record.tags.any { it.name.lowercase().contains(q) }
        }
    }
}
