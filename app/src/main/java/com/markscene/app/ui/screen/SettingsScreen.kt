package com.markscene.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markscene.app.R
import com.markscene.app.core.database.TagCorrectionEntity
import com.markscene.app.ui.util.SecureScreenEffect

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    hasLocalVlmModel: Boolean = false,
    localVlmModelName: String? = null,
    isLocalVlmModelDownloading: Boolean = false,
    localVlmDownloadedBytes: Long = 0L,
    localVlmTotalBytes: Long = 0L,
    localVlmDefaultModelName: String = "",
    localVlmDefaultModelSizeMb: Long = 0L,
    localVlmRequiresLicense: Boolean = false,
    localVlmLicenseUrl: String? = null,
    hasHuggingFaceToken: Boolean = false,
    isBiometricLockEnabled: Boolean,
    isTrueBlackEnabled: Boolean = false,
    isDynamicColorsEnabled: Boolean = false,
    isScreenshotBlockEnabled: Boolean = false,
    isExifStrippingEnabled: Boolean = true,
    isGalleryHidden: Boolean = true,
    isAutoLockEnabled: Boolean = false,
    tagCorrections: List<TagCorrectionEntity> = emptyList(),
    weeklyRecap: String? = null,
    achievementBadges: List<String> = emptyList(),
    onToggleBiometricLock: (Boolean) -> Unit,
    onToggleTrueBlack: (Boolean) -> Unit = {},
    onToggleDynamicColors: (Boolean) -> Unit = {},
    onToggleScreenshotBlock: (Boolean) -> Unit = {},
    onToggleExifStripping: (Boolean) -> Unit = {},
    onToggleGalleryHide: (Boolean) -> Unit = {},
    onToggleAutoLock: (Boolean) -> Unit = {},
    onImportLocalVlmModel: () -> Unit = {},
    onDeleteLocalVlmModel: () -> Unit = {},
    onSaveHuggingFaceToken: (String) -> Unit = {},
    onDeleteHuggingFaceToken: () -> Unit = {},
    onOpenModelLicense: () -> Unit = {},
    onOpenHuggingFaceTokenPage: () -> Unit = {},
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onExportCsv: () -> Unit = {},
    onExportMarkdown: () -> Unit = {},
    onDeleteTagCorrection: (String) -> Unit = {},
    externalMessage: String? = null,
    onMessageShown: () -> Unit,
    onOpenPrivacyNotice: () -> Unit,
    onOpenPrivacyDashboard: () -> Unit = {},
    onOpenTutorial: () -> Unit = {},
    onBack: () -> Unit
) {
    SecureScreenEffect()

    val context = LocalContext.current
    var hfTokenInput by rememberSaveable { mutableStateOf("") }
    var hfTokenVisible by rememberSaveable { mutableStateOf(false) }
    var resultMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            if (!weeklyRecap.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("주간 회고", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(weeklyRecap, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (achievementBadges.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("업적 배지", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            achievementBadges.forEach { badge ->
                                SuggestionChip(onClick = {}, label = { Text(badge) })
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("튜토리얼/가이드", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onOpenTutorial) { Text("열기") }
                }
            }

            // Local AI Section
            SettingsSection(title = stringResource(R.string.settings_ai_engine)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatusBadge(
                        label = if (hasLocalVlmModel) stringResource(R.string.settings_local_vlm_status_ready) else stringResource(R.string.settings_local_vlm_status_required),
                        isActive = hasLocalVlmModel
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.settings_local_vlm_title),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (hasLocalVlmModel) {
                                            stringResource(R.string.settings_local_vlm_ready, localVlmModelName ?: "local model")
                                        } else if (isLocalVlmModelDownloading) {
                                            formatLocalVlmProgress(
                                                context = context,
                                                downloaded = localVlmDownloadedBytes,
                                                total = localVlmTotalBytes
                                            )
                                        } else {
                                            stringResource(R.string.settings_local_vlm_desc)
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!hasLocalVlmModel && !isLocalVlmModelDownloading && localVlmDefaultModelName.isNotBlank() && localVlmDefaultModelSizeMb > 0) {
                                        Text(
                                            text = stringResource(
                                                R.string.settings_local_vlm_model_info,
                                                localVlmDefaultModelName,
                                                localVlmDefaultModelSizeMb
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (isLocalVlmModelDownloading && localVlmTotalBytes > 0) {
                                LinearProgressIndicator(
                                    progress = { (localVlmDownloadedBytes.toFloat() / localVlmTotalBytes.toFloat()).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else if (isLocalVlmModelDownloading) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }

                            Text(
                                text = stringResource(R.string.settings_model_catalog_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.settings_model_catalog_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            ModelCatalogItem(
                                title = stringResource(R.string.settings_model_catalog_recommended_title),
                                badge = stringResource(R.string.settings_model_catalog_badge_recommended),
                                description = stringResource(R.string.settings_model_catalog_recommended_desc),
                                meta = stringResource(
                                    R.string.settings_model_catalog_recommended_meta,
                                    localVlmDefaultModelName.ifBlank { "Gemma 3n E2B" },
                                    localVlmDefaultModelSizeMb
                                ),
                                isSelected = hasLocalVlmModel,
                                enabled = !isLocalVlmModelDownloading,
                                actionLabel = stringResource(
                                    when {
                                        isLocalVlmModelDownloading -> R.string.settings_local_vlm_downloading_short
                                        hasLocalVlmModel -> R.string.settings_local_vlm_redownload
                                        else -> R.string.settings_model_catalog_download_recommended
                                    }
                                ),
                                onClick = onImportLocalVlmModel
                            )

                            ModelCatalogItem(
                                title = stringResource(R.string.settings_model_catalog_light_title),
                                badge = stringResource(R.string.settings_model_catalog_badge_planned),
                                description = stringResource(R.string.settings_model_catalog_light_desc),
                                meta = stringResource(R.string.settings_model_catalog_light_meta),
                                isSelected = false,
                                enabled = false,
                                actionLabel = stringResource(R.string.settings_model_catalog_coming_soon),
                                onClick = {}
                            )

                            ModelCatalogItem(
                                title = stringResource(R.string.settings_model_catalog_custom_title),
                                badge = stringResource(R.string.settings_model_catalog_badge_review),
                                description = stringResource(R.string.settings_model_catalog_custom_desc),
                                meta = stringResource(R.string.settings_model_catalog_custom_meta),
                                isSelected = false,
                                enabled = false,
                                actionLabel = stringResource(R.string.settings_model_catalog_compatibility_check),
                                onClick = {}
                            )

                            if (localVlmRequiresLicense && !hasHuggingFaceToken) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.settings_local_vlm_license_required),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stringResource(R.string.settings_local_vlm_setup_intro),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        ModelSetupStep(
                                            index = 1,
                                            text = stringResource(R.string.settings_local_vlm_setup_step_license)
                                        )
                                        ModelSetupStep(
                                            index = 2,
                                            text = stringResource(R.string.settings_local_vlm_setup_step_token)
                                        )
                                        ModelSetupStep(
                                            index = 3,
                                            text = stringResource(R.string.settings_local_vlm_setup_step_download)
                                        )
                                        if (!localVlmLicenseUrl.isNullOrBlank()) {
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(onClick = onOpenModelLicense, shape = RoundedCornerShape(12.dp)) {
                                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(stringResource(R.string.settings_local_vlm_open_license))
                                                }
                                                OutlinedButton(onClick = onOpenHuggingFaceTokenPage, shape = RoundedCornerShape(12.dp)) {
                                                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(stringResource(R.string.settings_local_vlm_open_token_page))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // HuggingFace 토큰 입력 (Gemma 등 라이선스 게이트 모델용)
                            OutlinedTextField(
                                value = hfTokenInput,
                                onValueChange = { hfTokenInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.settings_local_vlm_hf_label)) },
                                placeholder = { Text(stringResource(R.string.settings_local_vlm_hf_placeholder)) },
                                supportingText = { Text(stringResource(R.string.settings_local_vlm_hf_help), style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                visualTransformation = if (hfTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done,
                                    autoCorrect = false
                                ),
                                trailingIcon = {
                                    Row {
                                        IconButton(onClick = { hfTokenVisible = !hfTokenVisible }) {
                                            Icon(
                                                imageVector = if (hfTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                        if (hfTokenInput.isNotBlank()) {
                                            IconButton(onClick = { hfTokenInput = "" }) {
                                                Icon(Icons.Default.Clear, contentDescription = null)
                                            }
                                        }
                                    }
                                }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (hfTokenInput.isNotBlank()) {
                                            onSaveHuggingFaceToken(hfTokenInput.trim())
                                            resultMessage = context.getString(R.string.settings_local_vlm_hf_saved)
                                            hfTokenInput = ""
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = hfTokenInput.isNotBlank()
                                ) { Text(stringResource(R.string.save)) }

                                if (hasHuggingFaceToken) {
                                    OutlinedButton(
                                        onClick = {
                                            onDeleteHuggingFaceToken()
                                            resultMessage = context.getString(R.string.settings_local_vlm_hf_deleted)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                    ) { Text(stringResource(R.string.delete)) }
                                }
                            }

                            if (hasLocalVlmModel) {
                                OutlinedButton(
                                    onClick = onDeleteLocalVlmModel,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(stringResource(R.string.settings_model_catalog_delete_current))
                                }
                            }

                            Text(
                                text = stringResource(R.string.settings_local_vlm_device_warning),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                }
            }

            // Tag Correction Section
            if (tagCorrections.isNotEmpty()) {
                SettingsSection(title = stringResource(R.string.settings_tag_dictionary)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                stringResource(R.string.settings_tag_dictionary_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            tagCorrections.forEach { correction ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(correction.originalName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(correction.correctedName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(onClick = { onDeleteTagCorrection(correction.originalName) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                if (correction != tagCorrections.last()) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            // Backup Section
            SettingsSection(title = stringResource(R.string.settings_data_backup)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsItem(
                        icon = Icons.Default.CloudDownload,
                        title = stringResource(R.string.settings_export_zip),
                        description = stringResource(R.string.settings_export_zip_desc),
                        onClick = onExportBackup
                    )
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = stringResource(R.string.settings_export_md),
                        description = stringResource(R.string.settings_export_md_desc),
                        onClick = onExportMarkdown
                    )
                    SettingsItem(
                        icon = Icons.Default.TableChart,
                        title = stringResource(R.string.settings_export_csv),
                        description = stringResource(R.string.settings_export_csv_desc),
                        onClick = onExportCsv
                    )
                    SettingsItem(
                        icon = Icons.Default.CloudUpload,
                        title = stringResource(R.string.settings_import),
                        description = stringResource(R.string.settings_import_desc),
                        onClick = onImportBackup
                    )
                }
            }

            // Security Section
            SettingsSection(title = stringResource(R.string.settings_security)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_lock), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_lock_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isBiometricLockEnabled,
                                onCheckedChange = onToggleBiometricLock,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.semantics { contentDescription = context.getString(R.string.settings_lock) }
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_screenshot_block), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_screenshot_block_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isScreenshotBlockEnabled,
                                onCheckedChange = onToggleScreenshotBlock,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.semantics { contentDescription = context.getString(R.string.settings_screenshot_block) }
                            )
                        }
                    }

                    // Auto-lock on background
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_auto_lock), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_auto_lock_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isAutoLockEnabled,
                                onCheckedChange = onToggleAutoLock,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.semantics { contentDescription = context.getString(R.string.settings_auto_lock) }
                            )
                        }
                    }

                    // EXIF stripping
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.ImageNotSupported, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_exif_stripping), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_exif_stripping_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isExifStrippingEnabled,
                                onCheckedChange = onToggleExifStripping,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.semantics { contentDescription = context.getString(R.string.settings_exif_stripping) }
                            )
                        }
                    }

                    // Gallery hide
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.HideImage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_gallery_hide), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_gallery_hide_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isGalleryHidden,
                                onCheckedChange = onToggleGalleryHide,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.semantics { contentDescription = context.getString(R.string.settings_gallery_hide) }
                            )
                        }
                    }

                    SettingsItem(
                        icon = Icons.Default.Dashboard,
                        title = stringResource(R.string.settings_privacy_dashboard),
                        description = stringResource(R.string.settings_privacy_dashboard_desc),
                        onClick = onOpenPrivacyDashboard
                    )

                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = stringResource(R.string.settings_privacy_policy),
                        description = stringResource(R.string.settings_privacy_policy_desc),
                        onClick = onOpenPrivacyNotice
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_restart_onboarding), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_restart_onboarding_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                            Text(
                                text = stringResource(R.string.settings_local_first_notice),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Appearance Section
            SettingsSection(title = stringResource(R.string.settings_appearance)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Dynamic Colors Toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_dynamic_colors), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_dynamic_colors_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isDynamicColorsEnabled,
                                onCheckedChange = onToggleDynamicColors,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    // True Black Toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.Contrast, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.settings_true_black), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.settings_true_black_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isTrueBlackEnabled,
                                onCheckedChange = onToggleTrueBlack,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }

            // Message Display
            val messageToShow = externalMessage ?: resultMessage
            messageToShow?.let {
                Snackbar(
                    modifier = Modifier.padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(it)
                        TextButton(onClick = { 
                            if (externalMessage != null) onMessageShown() else resultMessage = null 
                        }) {
                            Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                }
                
                if (externalMessage == null) {
                    LaunchedEffect(it) {
                        kotlinx.coroutines.delay(5000)
                        resultMessage = null
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModelCatalogItem(
    title: String,
    badge: String,
    description: String,
    meta: String,
    isSelected: Boolean,
    enabled: Boolean,
    actionLabel: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.Memory,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(badge) },
                            enabled = false
                        )
                    }
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(actionLabel)
            }
        }
    }
}

@Composable
private fun ModelSetupStep(index: Int, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Text(
                text = index.toString(),
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatusBadge(label: String, isActive: Boolean) {
    Surface(
        color = if (isActive) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun Modifier.size(size: androidx.compose.ui.unit.Dp): Modifier = this.width(size).height(size)

private fun formatLocalVlmProgress(
    context: android.content.Context,
    downloaded: Long,
    total: Long
): String {
    val downloadedText = formatBytesHuman(downloaded)
    if (total <= 0L) {
        return context.getString(R.string.settings_local_vlm_progress_unknown, downloadedText)
    }
    val percent = ((downloaded.toDouble() / total.toDouble()) * 100.0).toInt().coerceIn(0, 100)
    val totalText = formatBytesHuman(total)
    return context.getString(R.string.settings_local_vlm_progress, percent, downloadedText, totalText)
}

private fun formatBytesHuman(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    while (size >= 1024.0 && unitIndex < units.lastIndex) {
        size /= 1024.0
        unitIndex++
    }
    return if (unitIndex >= 2) String.format("%.1f %s", size, units[unitIndex])
    else String.format("%d %s", size.toLong(), units[unitIndex])
}
