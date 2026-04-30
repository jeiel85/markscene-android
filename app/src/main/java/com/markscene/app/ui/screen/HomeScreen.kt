package com.markscene.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCapturePhoto: () -> Unit,
    onImportPhoto: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MarkScene",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "사진을 검색 가능한 비주얼 메모로 기록하세요",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(onClick = onCapturePhoto, modifier = Modifier.fillMaxWidth()) {
            Text("Capture Photo")
        }
        Button(onClick = onImportPhoto, modifier = Modifier.fillMaxWidth()) {
            Text("Import Photo")
        }
        Button(onClick = onOpenSearch, modifier = Modifier.fillMaxWidth()) {
            Text("Record List / Search")
        }
        Button(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Settings")
        }
    }
}
