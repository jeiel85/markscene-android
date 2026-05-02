package com.markscene.app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.markscene.app.R
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
    var selectedSpace by remember { mutableStateOf<String?>(null) }
    val haptic = LocalHapticFeedback.current
    
    val spaces = remember(records) { 
        (records.mapNotNull { it.space } + listOf("책상", "주방", "창고", "아이방", "사무실", "거실")).distinct() 
    }
    
    val filteredRecords = remember(records, query, selectedSpace) {
        records.filter { record ->
            val matchesQuery = query.isBlank() || 
                record.title?.contains(query, ignoreCase = true) == true ||
                record.memo?.contains(query, ignoreCase = true) == true ||
                record.ocrText?.contains(query, ignoreCase = true) == true ||
                record.tags.any { it.name.contains(query, ignoreCase = true) }
            
            val matchesSpace = selectedSpace == null || record.space == selectedSpace
            matchesQuery && matchesSpace
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.list_title), 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBack() 
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onSearch(it)
                    },
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "기록 검색 입력창" },
                    placeholder = { Text(stringResource(R.string.list_search_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    ),
                    singleLine = true
                )
            }

            // Space Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedSpace == null,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedSpace = null 
                        },
                        label = { Text(stringResource(R.string.list_all_spaces)) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                items(spaces) { space ->
                    FilterChip(
                        selected = selectedSpace == space,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedSpace = if (selectedSpace == space) null else space 
                        },
                        label = { Text(space) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (filteredRecords.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.list_no_results),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(filteredRecords, key = { it.id }) { record ->
                        GalleryItem(
                            record = record,
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onOpenDetail(record.id) 
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryItem(
    record: PhotoRecord,
    onClick: () -> Unit
) {
    val spaceLabel = stringResource(R.string.create_field_space)
    val tagsLabel = stringResource(R.string.create_field_tags)
    val a11yDesc = buildString {
        append(record.title ?: stringResource(R.string.list_untitled))
        record.space?.let { append(", $spaceLabel: $it") }
        if (record.tags.isNotEmpty()) {
            append(", $tagsLabel: ${record.tags.joinToString { it.name }}")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .semantics { contentDescription = a11yDesc },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = record.imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.FillWidth
            )
            
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (record.space != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.secondary)
                        Text(
                            text = record.space,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!record.title.isNullOrBlank()) {
                    Text(
                        text = record.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                
                if (record.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        record.tags.take(2).forEach { tag ->
                            Text(
                                text = "#${tag.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
