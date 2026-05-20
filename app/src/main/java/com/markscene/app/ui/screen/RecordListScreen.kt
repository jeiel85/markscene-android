package com.markscene.app.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import com.markscene.app.ui.component.EmptyStateView

enum class LayoutType { GRID_2, GRID_3, LIST }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun RecordListScreen(
    records: List<PhotoRecord>,
    recentSearches: List<String> = emptyList(),
    allTags: List<String> = emptyList(),
    currentLayout: LayoutType = LayoutType.GRID_2,
    onSearch: (String) -> Unit,
    onDeleteRecords: (List<String>) -> Unit,
    onMoveToSpace: (List<String>, String) -> Unit,
    onOpenDetail: (String) -> Unit,
    onBack: () -> Unit,
    onLayoutChange: ((LayoutType) -> Unit)? = null,
    onClearRecentSearches: (() -> Unit)? = null
) {
    var query by remember { mutableStateOf("") }
    var selectedSpace by remember { mutableStateOf<String?>(null) }
    var isSearchFocused by remember { mutableStateOf(false) }
    var layoutType by remember { mutableStateOf(currentLayout) }
    val haptic = LocalHapticFeedback.current

    val selectedIds = remember { mutableStateListOf<String>() }
    val isSelectMode = selectedIds.isNotEmpty()
    var showMoveSpaceDialog by remember { mutableStateOf(false) }

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

    // Autocomplete suggestions
    val suggestions = remember(query, allTags, recentSearches) {
        if (query.isBlank()) {
            recentSearches
        } else {
            (allTags + recentSearches).distinct()
                .filter { it.contains(query, ignoreCase = true) && !it.equals(query, ignoreCase = true) }
                .take(5)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isSelectMode) Text("${selectedIds.size}개 선택됨", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    else Text(stringResource(R.string.list_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (isSelectMode) selectedIds.clear()
                        else { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onBack() }
                    }) {
                        Icon(imageVector = if (isSelectMode) Icons.Default.Close else Icons.Default.ArrowBack, contentDescription = stringResource(if (isSelectMode) R.string.close else R.string.back))
                    }
                },
                actions = {
                    if (isSelectMode) {
                        IconButton(onClick = { selectedIds.clear(); selectedIds.addAll(filteredRecords.map { it.id }) }) {
                            Icon(Icons.Default.SelectAll, contentDescription = "전체 선택")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (isSelectMode) {
                Surface(modifier = Modifier.fillMaxWidth().padding(16.dp), color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(24.dp), shadowElevation = 8.dp) {
                    Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showMoveSpaceDialog = true }) {
                                Icon(Icons.Default.DriveFileMove, contentDescription = null)
                                Text("이동", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onDeleteRecords(selectedIds.toList()); selectedIds.clear() }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text("삭제", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        Button(onClick = { selectedIds.clear() }) { Text("취소") }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!isSelectMode) {
                // Search Field with Suggestions
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Column {
                            TextField(
                                value = query,
                                onValueChange = { query = it; onSearch(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState: FocusState -> isSearchFocused = focusState.isFocused }
                                    .semantics { contentDescription = "기록 검색 입력창" },
                                placeholder = { Text(stringResource(R.string.list_search_placeholder)) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (query.isNotBlank()) {
                                        IconButton(onClick = { query = ""; onSearch("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = "검색어 지우기")
                                        }
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true
                            )

                            // Suggestions dropdown
                            if (isSearchFocused && suggestions.isNotEmpty()) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    if (query.isBlank() && recentSearches.isNotEmpty()) {
                                        Text(
                                            text = "최근 검색",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                        )
                                    }
                                    suggestions.forEach { suggestion ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    query = suggestion
                                                    onSearch(suggestion)
                                                    isSearchFocused = false
                                                }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (recentSearches.contains(suggestion)) Icons.Default.History else Icons.Default.Tag,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = suggestion,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    if (query.isBlank() && recentSearches.isNotEmpty()) {
                                        TextButton(
                                            onClick = { onClearRecentSearches?.invoke() },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("최근 검색 지우기", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Search filter chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickFilters = listOf(
                        "오늘" to "오늘",
                        "이번 주" to "이번 주",
                        "아이디어" to "아이디어",
                        "영수증" to "영수증",
                        "나중에 보기" to "나중에 보기",
                        "가족" to "가족",
                        "업무" to "업무"
                    )
                    items(quickFilters) { (label, value) ->
                        FilterChip(
                            selected = query.contains(value),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val newQuery = if (query.contains(value)) query.replace(value, "").trim() else value
                                onSearch(newQuery)
                                query = newQuery
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Space filter chips and layout selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { FilterChip(selected = selectedSpace == null, onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedSpace = null }, label = { Text(stringResource(R.string.list_all_spaces)) }, shape = RoundedCornerShape(12.dp)) }
                        items(spaces) { space -> FilterChip(selected = selectedSpace == space, onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedSpace = if (selectedSpace == space) null else space }, label = { Text(space) }, shape = RoundedCornerShape(12.dp)) }
                    }

                    // Layout selector
                    if (!isSelectMode) {
                        Row(
                            modifier = Modifier.padding(end = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    layoutType = LayoutType.GRID_2
                                    onLayoutChange?.invoke(layoutType)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "2열 그리드",
                                    tint = if (layoutType == LayoutType.GRID_2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    layoutType = LayoutType.GRID_3
                                    onLayoutChange?.invoke(layoutType)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewModule,
                                    contentDescription = "3열 그리드",
                                    tint = if (layoutType == LayoutType.GRID_3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    layoutType = LayoutType.LIST
                                    onLayoutChange?.invoke(layoutType)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewList,
                                    contentDescription = "목록 보기",
                                    tint = if (layoutType == LayoutType.LIST) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (filteredRecords.isEmpty()) {
                if (records.isEmpty() && query.isBlank() && selectedSpace == null) {
                    // First-time empty state with illustration
                    EmptyStateView(
                        onCaptureClick = onBack,
                        onImportClick = onBack
                    )
                } else {
                    // Search/filter empty state
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.empty_state_search_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.empty_state_search_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                when (layoutType) {
                    LayoutType.LIST -> {
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredRecords, key = { it.id }) { record ->
                                val isSelected = selectedIds.contains(record.id)
                                ListGalleryItem(
                                    record = record,
                                    isSelected = isSelected,
                                    isSelectMode = isSelectMode,
                                    onClick = {
                                        if (isSelectMode) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            if (isSelected) selectedIds.remove(record.id) else selectedIds.add(record.id)
                                        } else {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onOpenDetail(record.id)
                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelectMode) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            selectedIds.add(record.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    else -> {
                        val columns = if (layoutType == LayoutType.GRID_3) 3 else 2
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(columns),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalItemSpacing = 12.dp
                        ) {
                            items(filteredRecords, key = { it.id }) { record ->
                                val isSelected = selectedIds.contains(record.id)
                                if (!isSelectMode) {
                                    // Swipe to delete when not in select mode
                                    SwipeableGalleryItem(
                                        record = record,
                                        isSelected = isSelected,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onOpenDetail(record.id)
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            selectedIds.add(record.id)
                                        },
                                        onDelete = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onDeleteRecords(listOf(record.id))
                                        }
                                    )
                                } else {
                                    // Normal item in select mode
                                    GalleryItem(
                                        record = record,
                                        isSelected = isSelected,
                                        isSelectMode = isSelectMode,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            if (isSelected) selectedIds.remove(record.id) else selectedIds.add(record.id)
                                        },
                                        onLongClick = { }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showMoveSpaceDialog) {
        AlertDialog(onDismissRequest = { showMoveSpaceDialog = false }, title = { Text("공간 일괄 이동") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("선택한 ${selectedIds.size}개의 기록을 어디로 이동할까요?"); FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { spaces.forEach { space -> SuggestionChip(onClick = { onMoveToSpace(selectedIds.toList(), space); selectedIds.clear(); showMoveSpaceDialog = false }, label = { Text(space) }) } } } }, confirmButton = { TextButton(onClick = { showMoveSpaceDialog = false }) { Text(stringResource(R.string.cancel)) } })
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableGalleryItem(
    record: PhotoRecord,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { target ->
            when (target) {
                SwipeToDismissBoxValue.EndToStart,
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDeleteConfirm = true
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { distance -> distance * 0.5f }
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
            },
            title = { Text(stringResource(R.string.swipe_delete_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.swipe_delete_message,
                        record.title ?: stringResource(R.string.list_untitled)
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val isEnd = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
            val alignment = if (isEnd) Alignment.CenterEnd else Alignment.CenterStart
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = alignment
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        GalleryItem(
            record = record,
            isSelected = isSelected,
            isSelectMode = false,
            onClick = onClick,
            onLongClick = onLongClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListGalleryItem(
    record: PhotoRecord,
    isSelected: Boolean,
    isSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    // Performance: cache expensive computations
    val untitledText = stringResource(R.string.list_untitled)
    val a11yDesc = remember(record.title, record.space, untitledText) {
        buildString {
            append(record.title ?: untitledText)
            record.space?.let { append(", 공간: $it") }
        }
    }
    
    val tagsText = remember(record.tags) {
        if (record.tags.isNotEmpty()) {
            record.tags.take(3).joinToString(" ") { "#${it.name}" }
        } else null
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .semantics { contentDescription = a11yDesc },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = record.imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (record.space != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.secondary)
                        Text(text = record.space, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                if (!record.title.isNullOrBlank()) {
                    Text(text = record.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                }
                if (record.analysisStatus.name == "LocalComplete") {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "로컬 처리",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                if (tagsText != null) {
                    Text(
                        text = tagsText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            if (isSelectMode) {
                Checkbox(checked = isSelected, onCheckedChange = null)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GalleryItem(record: PhotoRecord, isSelected: Boolean, isSelectMode: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    // Performance: cache expensive computations
    val untitledText = stringResource(R.string.list_untitled)
    val a11yDesc = remember(record.title, record.space, untitledText) {
        buildString {
            append(record.title ?: untitledText)
            record.space?.let { append(", 공간: $it") }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .semantics { contentDescription = a11yDesc },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Box {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = record.space,
                                style = MaterialTheme.typography.labelSmall,
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
                    if (record.analysisStatus.name == "LocalComplete") {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "로컬 처리",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
            if (isSelectMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                )
            }
        }
    }
}
