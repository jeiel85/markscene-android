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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
                        "나의 기록", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("태그, 제목, 메모, 텍스트 검색...") },
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
                        onClick = { selectedSpace = null },
                        label = { Text("전체") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                items(spaces) { space ->
                    FilterChip(
                        selected = selectedSpace == space,
                        onClick = { selectedSpace = if (selectedSpace == space) null else space },
                        label = { Text(space) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (filteredRecords.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "결과가 없습니다.",
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
                            onClick = { onOpenDetail(record.id) }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = record.imageUri,
                contentDescription = record.title,
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
