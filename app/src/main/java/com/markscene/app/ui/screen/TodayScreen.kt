package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.markscene.app.R
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.ui.component.EmptyStateView
import com.markscene.app.ui.component.SceneCard
import com.markscene.app.ui.component.DailyRecapCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DateGroup(
    val label: String,
    val records: List<PhotoRecord>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    records: List<PhotoRecord>,
    onCapturePhoto: () -> Unit,
    onImportPhoto: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val dateGroups = remember(records) { groupRecordsByDate(records) }
    val todayCount = remember(records) {
        val todayStart = startOfDay(System.currentTimeMillis())
        records.count { it.createdAt >= todayStart }
    }
    val recapTopTags = remember(records) {
        val todayStart = startOfDay(System.currentTimeMillis())
        val todayRecords = records.filter { it.createdAt >= todayStart }
        todayRecords.flatMap { it.tags }.map { it.name }
            .groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }.take(5)
            .map { it.key }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.today_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (todayCount > 0) {
                            Text(
                                text = stringResource(R.string.today_record_count, todayCount),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    LocalProcessingBadge()
                    IconButton(onClick = onOpenSearch) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = onImportPhoto,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = stringResource(R.string.fab_import))
                }
                Spacer(modifier = Modifier.height(12.dp))
                FloatingActionButton(
                    onClick = onCapturePhoto,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = stringResource(R.string.fab_capture))
                }
            }
        }
    ) { paddingValues ->
        if (records.isEmpty()) {
            EmptyStateView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onCaptureClick = onCapturePhoto,
                onImportClick = onImportPhoto
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "daily_recap") {
                    DailyRecapCard(
                        recordCount = todayCount,
                        topTags = recapTopTags,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                dateGroups.forEach { group ->
                    item(key = "header_${group.label}") {
                        Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                            Text(
                                text = group.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.today_record_count, group.records.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(
                        items = group.records,
                        key = { it.id }
                    ) { record ->
                        SceneCard(
                            record = record,
                            onClick = { onOpenDetail(record.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }
    }
}

@Composable
private fun LocalProcessingBadge() {
    val description = stringResource(R.string.badge_local_processing_desc)
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(end = 4.dp)
            .semantics { contentDescription = description }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.badge_local_processing),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

private fun groupRecordsByDate(records: List<PhotoRecord>): List<DateGroup> {
    if (records.isEmpty()) return emptyList()

    val todayStart = startOfDay(System.currentTimeMillis())
    val yesterdayStart = todayStart - 86_400_000L

    val todayLabel = "오늘"
    val yesterdayLabel = "어제"
    val dateFormat = SimpleDateFormat("M월 d일 (E)", Locale.KOREAN)

    val grouped = records.groupBy { record ->
        when {
            record.createdAt >= todayStart -> todayLabel
            record.createdAt >= yesterdayStart -> yesterdayLabel
            else -> dateFormat.format(Date(record.createdAt))
        }
    }

    val orderedLabels = mutableListOf<String>()
    val todayRecords = grouped[todayLabel]
    if (todayRecords != null) orderedLabels.add(todayLabel)
    val yesterdayRecords = grouped[yesterdayLabel]
    if (yesterdayRecords != null) orderedLabels.add(yesterdayLabel)

    val otherLabels = grouped.keys
        .filter { it != todayLabel && it != yesterdayLabel }
        .sortedByDescending { label ->
            val recordsForLabel = grouped[label] ?: emptyList()
            recordsForLabel.maxOfOrNull { it.createdAt } ?: 0L
        }
    orderedLabels.addAll(otherLabels)

    return orderedLabels.mapNotNull { label ->
        val groupRecords = grouped[label] ?: return@mapNotNull null
        DateGroup(label = label, records = groupRecords.sortedByDescending { it.createdAt })
    }
}

private fun startOfDay(timestamp: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}