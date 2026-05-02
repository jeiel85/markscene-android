package com.markscene.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.markscene.app.core.model.PhotoRecord
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceTimelineScreen(
    spaceName: String,
    records: List<PhotoRecord>,
    onOpenDetail: (String) -> Unit,
    onCompare: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var isSelectMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(spaceName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("공간 히스토리", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (records.size >= 2) {
                        IconButton(onClick = { 
                            isSelectMode = !isSelectMode 
                            if (!isSelectMode) selectedIds.clear()
                        }) {
                            Icon(
                                imageVector = if (isSelectMode) Icons.Default.History else Icons.Default.Compare, 
                                contentDescription = "Toggle Compare Mode",
                                tint = if (isSelectMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (isSelectMode && selectedIds.size == 2) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("2개의 기록이 선택됨", fontWeight = FontWeight.Bold)
                        Button(onClick = { onCompare(selectedIds[0], selectedIds[1]) }) {
                            Text("지금 비교하기")
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (records.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("이 공간에 아직 기록이 없습니다.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                items(records) { record ->
                    TimelineItem(
                        record = record,
                        isSelected = selectedIds.contains(record.id),
                        isSelectMode = isSelectMode,
                        onClick = {
                            if (isSelectMode) {
                                if (selectedIds.contains(record.id)) {
                                    selectedIds.remove(record.id)
                                } else if (selectedIds.size < 2) {
                                    selectedIds.add(record.id)
                                }
                            } else {
                                onOpenDetail(record.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    record: PhotoRecord,
    isSelected: Boolean,
    isSelectMode: Boolean,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Left: Date & Dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = dateFormat.format(record.createdAt).substring(0, 3),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateFormat.format(record.createdAt).substring(4, 6),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        // Right: Photo Card
        Box(
            modifier = Modifier
                .weight(1f)
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AsyncImage(
                model = record.imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            if (isSelectMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                )
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                )
            }

            // Time Overlay
            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                Text(
                    text = dateFormat.format(record.createdAt).substring(8),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
