package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markscene.app.core.model.PhotoRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    records: List<PhotoRecord>,
    onSearch: (String) -> Unit,
    onDeleteRecord: (String) -> Unit,
    onOpenDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Record List / Search", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onSearch(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("태그, 제목, 메모 검색") }
        )

        if (records.isEmpty()) {
            Text(
                text = "저장된 비주얼 메모가 없습니다.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(records, key = { it.id }) { record ->
                    RecordItem(
                        record = record,
                        onDeleteRecord = onDeleteRecord,
                        onOpenDetail = onOpenDetail
                    )
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
private fun RecordItem(
    record: PhotoRecord,
    onDeleteRecord: (String) -> Unit,
    onOpenDetail: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = record.title ?: "Untitled Record", style = MaterialTheme.typography.titleMedium)
            Text(text = "imageUri: ${record.imageUri}", style = MaterialTheme.typography.bodySmall)
            if (!record.memo.isNullOrBlank()) {
                Text(text = record.memo, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                record.tags.take(4).forEach { tag ->
                    AssistChip(onClick = {}, label = { Text(tag.name) })
                }
            }
            Button(onClick = { onOpenDetail(record.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("Open Detail")
            }
            Button(onClick = { onDeleteRecord(record.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("Delete Record")
            }
        }
    }
}
