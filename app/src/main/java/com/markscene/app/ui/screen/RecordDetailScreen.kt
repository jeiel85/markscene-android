package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.markscene.app.ai.provider.MockAdvancedVisionProvider
import com.markscene.app.ai.provider.MockAdvancedAnalysisResult
import com.markscene.app.core.model.AdvancedAnalysis
import com.markscene.app.core.model.PhotoRecord

@Composable
fun RecordDetailScreen(
    record: PhotoRecord,
    latestAnalysis: AdvancedAnalysis?,
    onApplyAdvancedAnalysis: (MockAdvancedAnalysisResult) -> Unit,
    onBack: () -> Unit
) {
    val mockProvider = remember { MockAdvancedVisionProvider() }
    var analysisResult by remember { mutableStateOf<MockAdvancedAnalysisResult?>(null) }
    var showConsentDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Record Detail", style = MaterialTheme.typography.headlineSmall)
        AsyncImage(model = record.imageUri, contentDescription = "record image", modifier = Modifier.fillMaxWidth())
        Text(record.title ?: "Untitled", style = MaterialTheme.typography.titleMedium)
        Text(record.memo ?: "메모 없음", style = MaterialTheme.typography.bodyMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(record.tags) { tag -> AssistChip(onClick = {}, label = { Text(tag.name) }) }
        }

        latestAnalysis?.let {
            Text("Saved Analysis", style = MaterialTheme.typography.titleMedium)
            Text(it.sceneSummary)
        }

        Button(onClick = { showConsentDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Run Advanced Analysis (Mock)")
        }

        analysisResult?.let { result ->
            Text("Scene Summary", style = MaterialTheme.typography.titleMedium)
            Text(result.sceneSummary)
            Text("Suggested Tags", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(result.suggestedTags) { tag -> AssistChip(onClick = {}, label = { Text(tag) }) }
            }
            Text(result.warnings.joinToString("\n"), style = MaterialTheme.typography.bodySmall)
            Button(onClick = { onApplyAdvancedAnalysis(result) }, modifier = Modifier.fillMaxWidth()) {
                Text("Apply Analysis To Record")
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }

    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = { showConsentDialog = false },
            title = { Text("외부 분석 안내") },
            text = { Text("고급 분석을 실행하면 선택한 이미지와 프롬프트가 AI 제공자에 전송될 수 있습니다.") },
            confirmButton = {
                Button(onClick = {
                    analysisResult = mockProvider.analyze(record)
                    showConsentDialog = false
                }) { Text("Analyze") }
            },
            dismissButton = {
                Button(onClick = { showConsentDialog = false }) { Text("Cancel") }
            }
        )
    }
}
