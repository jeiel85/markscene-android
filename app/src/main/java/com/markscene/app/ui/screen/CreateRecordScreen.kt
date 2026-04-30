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
fun CreateRecordScreen(
    source: String,
    onBack: () -> Unit
) {
    val sourceLabel = when (source) {
        "capture" -> "Capture Photo"
        "import" -> "Import Photo"
        else -> "Unknown"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create Record", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "$sourceLabel 진입 플로우 준비 중입니다.",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "다음 단계에서 CameraX/Photo Picker를 연결합니다.",
            style = MaterialTheme.typography.bodySmall
        )
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
