package com.markscene.app.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.common.util.concurrent.ListenableFuture
import com.markscene.app.ai.provider.LocalImageTagger
import com.markscene.app.ai.provider.TextRecognizer
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import java.io.File
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRecordScreen(
    source: String,
    localImageTagger: LocalImageTagger,
    textRecognizer: TextRecognizer,
    onSave: (PhotoRecord) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var ocrText by remember { mutableStateOf<String?>(null) }
    var customTag by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    val editableTags = remember { mutableStateListOf<String>() }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) statusText = "카메라 권한이 필요합니다."
    }

    LaunchedEffect(imageUri) {
        val uri = imageUri ?: return@LaunchedEffect
        isAnalyzing = true
        statusText = "AI가 태그와 텍스트를 분석 중입니다..."
        try {
            val tagsResult = localImageTagger.generateTags(uri)
            val ocrResult = textRecognizer.recognizeText(uri)
            editableTags.clear()
            editableTags.addAll(tagsResult.map { it.name })
            ocrText = ocrResult.getOrNull()
            statusText = "분석이 완료되었습니다."
        } catch (e: Exception) {
            statusText = "분석 중 오류 발생: ${e.message}"
        } finally {
            isAnalyzing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 메모 기록", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val selectedUri = imageUri ?: return@TextButton
                            val now = System.currentTimeMillis()
                            val recordId = UUID.randomUUID().toString()
                            val tags = editableTags.map { tagName ->
                                PhotoTag(
                                    id = UUID.randomUUID().toString(),
                                    recordId = recordId,
                                    name = tagName,
                                    rawName = null,
                                    source = TagSource.LocalImageLabel,
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
                                    ocrText = ocrText,
                                    tags = tags
                                )
                            )
                        },
                        enabled = imageUri != null && !isAnalyzing
                    ) {
                        Text("저장", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Image Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { imageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.White)
                    }
                } else {
                    if (source == SOURCE_CAPTURE) {
                        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        if (granted) {
                            CameraCapturePreview(
                                onCaptured = { imageUri = it },
                                onError = { statusText = it }
                            )
                        } else {
                            Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                Text("카메라 권한 허용")
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.clickable { 
                                pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                            }
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("사진 선택하기", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            if (statusText.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isAnalyzing) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(statusText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Input Fields
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("제목") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("메모") },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            }

            // Tags Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("태그", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    editableTags.forEach { tag ->
                        InputChip(
                            selected = true,
                            onClick = { editableTags.remove(tag) },
                            label = { Text(tag) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }

                OutlinedTextField(
                    value = customTag,
                    onValueChange = { customTag = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("태그 직접 입력 후 엔터") },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            val normalized = customTag.trim().lowercase()
                            if (normalized.isNotBlank() && !editableTags.contains(normalized)) {
                                editableTags.add(normalized)
                                customTag = ""
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    },
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun CameraCapturePreview(
    onCaptured: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { ContextCompat.getMainExecutor(context) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }

    LaunchedEffect(lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = providerFuture.await(cameraExecutor)
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        
        // Shutter Button
        FloatingActionButton(
            onClick = {
                val captureTarget = createCaptureTarget(context)
                val output = ImageCapture.OutputFileOptions.Builder(captureTarget.file).build()
                imageCapture.takePicture(
                    output,
                    cameraExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onCaptured(captureTarget.uri)
                        }
                        override fun onError(exception: ImageCaptureException) {
                            onError("촬영 실패: ${exception.message}")
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = "Capture", modifier = Modifier.size(32.dp))
        }
    }
}

private data class CaptureTarget(val file: File, val uri: Uri)

private fun createCaptureTarget(context: Context): CaptureTarget {
    val directory = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File(directory, "capture_${System.currentTimeMillis()}.jpg")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    return CaptureTarget(file = file, uri = uri)
}

private const val SOURCE_CAPTURE = "capture"
private const val SOURCE_IMPORT = "import"

private suspend fun ListenableFuture<ProcessCameraProvider>.await(
    executor: java.util.concurrent.Executor
): ProcessCameraProvider = suspendCoroutine { continuation ->
    addListener({ continuation.resume(get()) }, executor)
}

private fun Modifier.size(size: androidx.compose.ui.unit.Dp): Modifier = this.width(size).height(size)
