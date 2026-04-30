package com.markscene.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markscene.app.ai.provider.LocalImageTagger
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import java.util.UUID

@Composable
fun CreateRecordScreen(
    source: String,
    localImageTagger: LocalImageTagger,
    onSave: (PhotoRecord) -> Unit,
    onBack: () -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var customTag by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("사진을 선택하면 태그 초안을 생성합니다.") }
    val editableTags = remember { mutableStateListOf<String>() }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    LaunchedEffect(source) {
        if (source == "capture" && imageUri == null) {
            imageUri = Uri.parse("capture://mock/${System.currentTimeMillis()}")
            statusText = "캡처 흐름은 임시 mock URI로 연결되었습니다."
        }
    }

    LaunchedEffect(imageUri) {
        val uri = imageUri ?: return@LaunchedEffect
        statusText = "로컬 태그 초안을 생성 중입니다..."
        val tags = localImageTagger.generateTags(uri).map { it.name }
        editableTags.clear()
        editableTags.addAll(tags)
        statusText = "태그 초안이 생성되었습니다. 필요한 경우 수정하세요."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Create Record", style = MaterialTheme.typography.headlineSmall)
        Text(text = statusText, style = MaterialTheme.typography.bodySmall)

        if (source == "import") {
            Button(
                onClick = {
                    pickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Import Photo")
            }
        }

        Text(
            text = "선택 이미지: ${imageUri?.toString() ?: "없음"}",
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("제목 (선택)") }
        )

        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("메모 (선택)") }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            editableTags.forEach { tag ->
                AssistChip(
                    onClick = { editableTags.remove(tag) },
                    label = { Text(tag) }
                )
            }
        }

        OutlinedTextField(
            value = customTag,
            onValueChange = { customTag = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("태그 추가") }
        )

        Button(
            onClick = {
                val normalized = customTag.trim().lowercase()
                if (normalized.isNotBlank() && normalized !in editableTags) {
                    editableTags.add(normalized)
                    customTag = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Tag")
        }

        Button(
            onClick = {
                val selectedUri = imageUri ?: return@Button
                val now = System.currentTimeMillis()
                val recordId = UUID.randomUUID().toString()
                val tags = editableTags.map { tagName ->
                    PhotoTag(
                        id = UUID.randomUUID().toString(),
                        recordId = recordId,
                        name = tagName,
                        rawName = null,
                        source = TagSource.Mock,
                        confidence = null,
                        userConfirmed = true,
                        createdAt = now
                    )
                }
                onSave(
                    PhotoRecord(
                        id = recordId,
                        imageUri = selectedUri.toString(),
                        title = title.ifBlank { null },
                        memo = memo.ifBlank { null },
                        createdAt = now,
                        updatedAt = now,
                        analysisStatus = AnalysisStatus.LocalComplete,
                        tags = tags
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = imageUri != null
        ) {
            Text("Save Record")
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
