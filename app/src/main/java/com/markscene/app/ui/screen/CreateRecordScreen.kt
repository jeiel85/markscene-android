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
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.common.util.concurrent.ListenableFuture
import com.markscene.app.R
import com.markscene.app.ai.provider.LocalImageTagger
import com.markscene.app.ai.provider.TextRecognizer
import com.markscene.app.core.model.AnalysisStatus
import com.markscene.app.core.model.MemoryType
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.core.model.PhotoTag
import com.markscene.app.core.model.TagSource
import com.markscene.app.ui.util.GalleryHideHelper
import com.markscene.app.ui.util.ImageCropper
import com.markscene.app.ui.util.ImageOptimizer
import com.markscene.app.ui.util.SecureScreenEffect
import com.markscene.app.ui.component.MemoryTypeChipSection
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Draggable Tag Flow Component
 * Allows reordering tags via long-press and drag
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun DraggableTagFlow(
    tags: List<String>,
    onTagsReordered: (List<String>) -> Unit,
    onTagRemoved: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEachIndexed { index, tag ->
            val isDragging = draggedIndex == index

            InputChip(
                selected = true,
                onClick = { onTagRemoved(tag) },
                label = { Text(tag) },
                trailingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                modifier = if (isDragging) {
                    Modifier.padding(start = dragOffset.dp)
                } else {
                    Modifier
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRecordScreen(
    source: String,
    initialImageUri: Uri? = null,
    localImageTagger: LocalImageTagger,
    textRecognizer: TextRecognizer,
    onSave: (PhotoRecord) -> Unit,
    onLearnTagCorrection: (original: String, corrected: String) -> Unit = { _, _ -> },
    onSaveMemoryTypes: (recordId: String, memoryTypes: List<String>, isWorthRecalling: Boolean) -> Unit = { _, _, _ -> },
    onBack: () -> Unit
) {
    SecureScreenEffect()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val defaultSpaces = listOf("책상", "주방", "창고", "아이방", "사무실", "거실", "기타")

    var imageUri by remember { mutableStateOf<Uri?>(initialImageUri) }
    var title by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var selectedSpace by remember { mutableStateOf<String?>(null) }
    var audioMemoUri by remember { mutableStateOf<Uri?>(null) }
    var ocrText by remember { mutableStateOf<String?>(null) }
    var customTag by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showCameraRationale by remember { mutableStateOf(false) }
    var showAudioNotice by remember { mutableStateOf(false) }
    
    val editableTags = remember { mutableStateListOf<String>() }
    val originalAiSuggestions = remember { mutableStateListOf<String>() }
    val selectedMemoryTypes = remember { mutableStateListOf<MemoryType>() }
    var isWorthRecalling by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
        if (uri != null) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) statusText = context.getString(R.string.create_camera_permission)
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        audioMemoUri = uri
        if (uri != null) {
            statusText = "오디오 메모가 첨부되었습니다."
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val analyzingMsg = stringResource(R.string.create_analyzing)
    val doneMsg = stringResource(R.string.create_analysis_done)

    LaunchedEffect(imageUri) {
        val uri = imageUri ?: return@LaunchedEffect
        isAnalyzing = true
        statusText = analyzingMsg
        try {
            val tagsResult = localImageTagger.generateTags(uri)
            val ocrResult = textRecognizer.recognizeText(uri)
            
            val suggestionNames = tagsResult.map { it.name }
            editableTags.clear()
            editableTags.addAll(suggestionNames)
            
            originalAiSuggestions.clear()
            originalAiSuggestions.addAll(suggestionNames)
            
            ocrText = ocrResult.getOrNull()
            statusText = doneMsg
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (e: Exception) {
            statusText = "${context.getString(R.string.error)}: ${e.message}"
        } finally {
            isAnalyzing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val selectedUri = imageUri ?: return@TextButton
                            isSaving = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch {
                                val now = System.currentTimeMillis()
                                val recordId = UUID.randomUUID().toString()
                                
                                val removedSuggestions = originalAiSuggestions.filter { !editableTags.contains(it) }
                                val addedTags = editableTags.filter { !originalAiSuggestions.contains(it) }
                                if (removedSuggestions.size == 1 && addedTags.size == 1) {
                                    onLearnTagCorrection(removedSuggestions.first(), addedTags.first())
                                }
                                
                                val optimizedFile = ImageOptimizer.optimize(
                                    context = context,
                                    inputUri = selectedUri,
                                    targetFileName = "$recordId.webp"
                                )
                                
                                if (optimizedFile != null) {
                                    GalleryHideHelper.ensureNoMediaForRecordsDir(context)
                                    val tags = editableTags.map { tagName ->
                                        PhotoTag(
                                            id = UUID.randomUUID().toString(),
                                            recordId = recordId,
                                            name = tagName,
                                            rawName = null,
                                            source = if (originalAiSuggestions.contains(tagName)) TagSource.LocalImageLabel else TagSource.User,
                                            confidence = null,
                                            userConfirmed = true,
                                            createdAt = now
                                        )
                                    }
                                    if (selectedMemoryTypes.isNotEmpty() || isWorthRecalling) {
                                                        onSaveMemoryTypes(recordId, selectedMemoryTypes.map { it.name }, isWorthRecalling)
                                                    }
                                                    onSave(
                                        PhotoRecord(
                                            id = recordId,
                                            imageUri = Uri.fromFile(optimizedFile).toString(),
                                            audioMemoUri = audioMemoUri?.toString(),
                                            title = title.ifBlank { null },
                                            memo = memo.ifBlank { null },
                                            space = selectedSpace,
                                            createdAt = now,
                                            updatedAt = now,
                                            analysisStatus = AnalysisStatus.LocalComplete,
                                            ocrText = ocrText,
                                            tags = tags
                                        )
                                    )
                                } else {
                                    statusText = context.getString(R.string.create_save_failed)
                                    isSaving = false
                                }
                            }
                        },
                        enabled = imageUri != null && !isAnalyzing && !isSaving
                    ) {
                        if (isSaving) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        else Text(stringResource(R.string.save), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            val imageAreaDesc = stringResource(R.string.create_image_area)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .semantics { contentDescription = imageAreaDesc },
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
                        onClick = {
                            val currentUri = imageUri ?: return@IconButton
                            val croppedFile = ImageCropper.cropCenterSquare(context, currentUri)
                            if (croppedFile != null) {
                                imageUri = Uri.fromFile(croppedFile)
                                statusText = "이미지를 정사각형으로 크롭했습니다."
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } else {
                                statusText = context.getString(R.string.create_save_failed)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .semantics { contentDescription = "이미지 크롭" }
                    ) {
                        Icon(Icons.Default.Crop, contentDescription = null, tint = Color.White)
                    }
                    IconButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            imageUri = null 
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .semantics { contentDescription = context.getString(R.string.delete) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    }
                } else {
                    if (source == SOURCE_CAPTURE) {
                        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        if (granted) {
                            CameraCapturePreview(
                                onCaptured = { 
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    imageUri = it 
                                },
                                onError = { statusText = it }
                            )
                        } else {
                            Button(
                                onClick = { showCameraRationale = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.create_camera_permission))
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.clickable { 
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                            }
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Text(stringResource(R.string.home_import), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Audio memo attachment
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = if (audioMemoUri != null) "오디오 메모 첨부됨" else "오디오 메모 첨부",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAudioNotice = true }) {
                            Text(if (audioMemoUri != null) "변경" else "첨부")
                        }
                        if (audioMemoUri != null) {
                            TextButton(onClick = { audioMemoUri = null }) { Text("삭제") }
                        }
                    }
                }
            }

            if (showCameraRationale) {
                AlertDialog(
                    onDismissRequest = { showCameraRationale = false },
                    title = { Text("카메라 권한 안내") },
                    text = { Text("촬영 기능을 위해 카메라 권한이 필요합니다. 촬영된 이미지는 사용자가 저장할 때만 기록됩니다.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showCameraRationale = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }) { Text("권한 요청") }
                    },
                    dismissButton = { TextButton(onClick = { showCameraRationale = false }) { Text("취소") } }
                )
            }

            if (showAudioNotice) {
                AlertDialog(
                    onDismissRequest = { showAudioNotice = false },
                    title = { Text("오디오 메모 첨부 안내") },
                    text = { Text("선택한 오디오 파일은 이 기록에만 연결되며 외부 서버로 자동 전송되지 않습니다.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showAudioNotice = false
                            audioPickerLauncher.launch(arrayOf("audio/*"))
                        }) { Text("확인") }
                    },
                    dismissButton = { TextButton(onClick = { showAudioNotice = false }) { Text("취소") } }
                )
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
                Text(stringResource(R.string.create_field_space), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    defaultSpaces.forEach { space ->
                        FilterChip(
                            selected = selectedSpace == space,
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedSpace = if (selectedSpace == space) null else space 
                            },
                            label = { Text(space) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.create_field_title)) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.create_field_memo)) },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            }

            // Tags Section with Drag & Drop
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.create_field_tags), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (editableTags.size > 1) {
                        Text(
                            text = "길게 눌러 순서 변경",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Draggable Tags Flow
                DraggableTagFlow(
                    tags = editableTags.toList(),
                    onTagsReordered = { newOrder ->
                        editableTags.clear()
                        editableTags.addAll(newOrder)
                    },
                    onTagRemoved = { tag ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        editableTags.remove(tag)
                    }
                )

                OutlinedTextField(
                    value = customTag,
                    onValueChange = { customTag = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.create_tag_placeholder)) },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            val normalized = customTag.trim().lowercase()
                            if (normalized.isNotBlank() && !editableTags.contains(normalized)) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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

            // Memory Type Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "이 장면은 어떤 기억인가요?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                MemoryTypeChipSection(
                    selectedTypes = selectedMemoryTypes,
                    onToggle = { type ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (type in selectedMemoryTypes) {
                            selectedMemoryTypes.remove(type)
                        } else {
                            selectedMemoryTypes.add(type)
                        }
                    }
                )
            }

            // Recall Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "다시 볼 기록으로 저장",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Switch(
                    checked = isWorthRecalling,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isWorthRecalling = it
                    }
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
    val haptic = LocalHapticFeedback.current
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
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                            onError("${context.getString(R.string.error)}: ${exception.message}")
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .semantics { contentDescription = context.getString(R.string.home_capture) },
            containerColor = Color.White,
            contentColor = Color(0xFF2D5AFE),
            shape = CircleShape
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(32.dp))
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
