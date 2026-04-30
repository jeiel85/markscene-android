package com.markscene.app.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.markscene.app.ai.provider.LocalImageTagger
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import java.io.File
import java.util.UUID

@Composable
fun CreateRecordScreen(
    source: String,
    localImageTagger: LocalImageTagger,
    onSave: (PhotoRecord) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
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

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = pendingCaptureUri
            statusText = "사진 촬영이 완료되었습니다."
        } else {
            statusText = "사진 촬영이 취소되었거나 실패했습니다."
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val targetUri = createCaptureUri(context)
            pendingCaptureUri = targetUri
            takePictureLauncher.launch(targetUri)
        } else {
            statusText = "카메라 권한이 필요합니다."
        }
    }

    LaunchedEffect(source) {
        if (source == "import") {
            statusText = "가져올 사진을 선택하세요."
        } else {
            statusText = "촬영 버튼으로 사진을 찍으세요."
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
            ) { Text("Import Photo") }
        }

        if (source == "capture") {
            Button(
                onClick = {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (granted) {
                        val targetUri = createCaptureUri(context)
                        pendingCaptureUri = targetUri
                        takePictureLauncher.launch(targetUri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Capture Photo") }
        }

        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "selected image",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = "선택 이미지: ${imageUri?.toString() ?: "없음"}",
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("제목 (선택)") })
        OutlinedTextField(value = memo, onValueChange = { memo = it }, modifier = Modifier.fillMaxWidth(), label = { Text("메모 (선택)") })

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            editableTags.forEach { tag ->
                AssistChip(onClick = { editableTags.remove(tag) }, label = { Text(tag) })
            }
        }

        OutlinedTextField(value = customTag, onValueChange = { customTag = it }, modifier = Modifier.fillMaxWidth(), label = { Text("태그 추가") })

        Button(
            onClick = {
                val normalized = customTag.trim().lowercase()
                if (normalized.isNotBlank() && normalized !in editableTags) {
                    editableTags.add(normalized)
                    customTag = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add Tag") }

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
        ) { Text("Save Record") }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

private fun createCaptureUri(context: Context): Uri {
    val directory = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File(directory, "capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
