package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.markscene.app.core.model.PhotoRecord

@Composable
fun RecordDetailScreen(
    record: PhotoRecord,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Record Detail", style = MaterialTheme.typography.headlineSmall)
        AsyncImage(
            model = record.imageUri,
            contentDescription = "record image",
            modifier = Modifier.fillMaxWidth()
        )
        Text(record.title ?: "Untitled", style = MaterialTheme.typography.titleMedium)
        Text(record.memo ?: "메모 없음", style = MaterialTheme.typography.bodyMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(record.tags) { tag ->
                AssistChip(onClick = {}, label = { Text(tag.name) })
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
