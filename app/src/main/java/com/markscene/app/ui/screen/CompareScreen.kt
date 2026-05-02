package com.markscene.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.markscene.app.R
import com.markscene.app.core.model.PhotoRecord
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    record1: PhotoRecord,
    record2: PhotoRecord,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    // Sort by date: record1 is older, record2 is newer
    val older = if (record1.createdAt < record2.createdAt) record1 else record2
    val newer = if (record1.createdAt < record2.createdAt) record2 else record1
    
    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.compare_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Comparison Header
            val spaceName = older.space ?: stringResource(R.string.detail_unassigned_space)
            Text(
                text = "$spaceName ${stringResource(R.string.compare_suffix)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Before Card
            CompareCard(
                label = "BEFORE",
                date = dateFormat.format(older.createdAt),
                record = older,
                labelColor = Color(0xFF64748B)
            )

            // Center Divider Icon
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp).size(24.dp),
                        tint = Color.White
                    )
                }
            }

            // After Card
            CompareCard(
                label = "AFTER",
                date = dateFormat.format(newer.createdAt),
                record = newer,
                labelColor = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CompareCard(
    label: String,
    date: String,
    record: PhotoRecord,
    labelColor: Color
) {
    val a11yDesc = "$label, $date: ${record.title ?: "제목 없음"}"
    
    Card(
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = a11yDesc },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                AsyncImage(
                    model = record.imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                    color = labelColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(record.title ?: stringResource(R.string.list_untitled), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (!record.memo.isNullOrBlank()) {
                    Text(
                        text = record.memo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
