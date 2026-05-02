package com.markscene.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.markscene.app.ai.provider.MockAdvancedVisionProvider
import com.markscene.app.ai.provider.MockAdvancedAnalysisResult
import com.markscene.app.core.model.AdvancedAnalysis
import com.markscene.app.core.model.PhotoRecord
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecordDetailScreen(
    record: PhotoRecord,
    latestAnalysis: AdvancedAnalysis?,
    historyRecords: List<PhotoRecord> = emptyList(),
    onRunAdvancedAnalysis: suspend (PhotoRecord) -> MockAdvancedAnalysisResult,
    onApplyAdvancedAnalysis: (MockAdvancedAnalysisResult) -> Unit,
    onDeleteRecord: (String) -> Unit,
    onOpenOtherRecord: (String) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var analysisResult by remember { mutableStateOf<MockAdvancedAnalysisResult?>(null) }
    var showConsentDialog by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack, 
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onDeleteRecord(record.id) }, 
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {
                AsyncImage(
                    model = record.imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Bottom Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                startY = 800f
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = record.title ?: "제목 없는 기록",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        record.space?.let { space ->
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        size = 16.dp,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = space,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        if (!record.memo.isNullOrBlank()) {
                            Text(
                                text = record.memo,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            record.tags.forEach { tag ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(tag.name) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        labelColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }

                // AI Insights Section
                Text(
                    text = "AI Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (latestAnalysis != null) {
                    AIInsightCard(summary = latestAnalysis.sceneSummary)
                } else if (analysisResult != null) {
                    AIInsightCard(
                        summary = analysisResult!!.sceneSummary,
                        suggestedTags = analysisResult!!.suggestedTags,
                        onApply = { onApplyAdvancedAnalysis(analysisResult!!) }
                    )
                } else {
                    OutlinedButton(
                        onClick = { showConsentDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(16.dp),
                        enabled = !isAnalyzing
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(12.dp))
                            Text("분석 중...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("고급 AI 분석 실행하기")
                        }
                    }
                }
                
                // Location History Section
                if (historyRecords.size > 1) {
                    Text(
                        text = "위치 히스토리",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        historyRecords.filter { it.id != record.id }.forEach { history ->
                            HistoryItem(
                                record = history,
                                onClick = { onOpenOtherRecord(history.id) }
                            )
                        }
                    }
                }
                
                statusMessage?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = { showConsentDialog = false },
            title = { Text("AI 고급 분석 안내") },
            text = { Text("이 사진을 분석하기 위해 외부 AI 엔진으로 전송합니다. 계속하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    showConsentDialog = false
                    isAnalyzing = true
                    scope.launch {
                        try {
                            analysisResult = onRunAdvancedAnalysis(record)
                            statusMessage = "분석이 완료되었습니다."
                        } catch (e: Exception) {
                            statusMessage = "분석 중 오류 발생: ${e.message}"
                        } finally {
                            isAnalyzing = false
                        }
                    }
                }) { Text("분석 시작") }
            },
            dismissButton = {
                TextButton(onClick = { showConsentDialog = false }) { Text("취소") }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AIInsightCard(
    summary: String,
    suggestedTags: List<String>? = null,
    onApply: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("장면 요약", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            if (suggestedTags != null) {
                HorizontalDivider(modifier = Modifier.graphicsLayer { alpha = 0.1f })
                Text("추천 태그", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestedTags.forEach { tag ->
                        SuggestionChip(onClick = {}, label = { Text(tag) })
                    }
                }
                
                Button(
                    onClick = { onApply?.invoke() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("태그 적용하기")
                }
            }
        }
    }
}

private fun Modifier.size(size: androidx.compose.ui.unit.Dp): Modifier = this.width(size).height(size)
