package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.markscene.app.R
import com.markscene.app.core.model.MemoryType
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.ui.component.SceneCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecallScreen(
    recallRecords: List<PhotoRecord>,
    onOpenDetail: (String) -> Unit
) {
    val recallSections = remember(recallRecords) { buildRecallSections(recallRecords) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recall Box",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        if (recallRecords.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "아직 다시 볼 기록이 없습니다.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "아이디어나 나중에 확인할 장면을 저장하면 이곳에 모입니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recallSections.forEach { section ->
                    item(key = section.key) {
                        Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${section.records.size}개의 기록",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(items = section.records, key = { "recall_${it.id}" }) { record ->
                        SceneCard(
                            record = record,
                            onClick = { onOpenDetail(record.id) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

data class RecallSection(
    val key: String,
    val title: String,
    val records: List<PhotoRecord>
)

private val RECALL_KEYWORDS = listOf("나중에", "확인", "만들기", "사야 함", "정리", "TODO")

private fun buildRecallSections(records: List<PhotoRecord>): List<RecallSection> {
    // TODO: In the future, combine with memoryContext data for proper sectioning
    // For now, show all recall records in a single list
    return listOf(
        RecallSection(
            key = "all",
            title = "다시 볼 기록",
            records = records
        )
    )
}
